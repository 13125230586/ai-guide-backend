#!/usr/bin/env python3
"""
Full API smoke tester for ai-guide-backend.

What it does:
    1. Logs in as both tourist and admin users with cookie-based sessions.
    2. Exercises every documented controller endpoint in the backend.
    3. Uses real IDs from the seeded database / live API responses.
    4. Prints each response to stdout and writes JSON + Markdown reports.

Usage:
    python3 scripts/test_api_endpoints.py

Environment variables:
    BASE_URL=http://127.0.0.1:8137
    DB_HOST=127.0.0.1
    DB_PORT=3306
    DB_USER=root
    DB_PASSWORD=hcc153502
    DB_NAME=ai_guide_platform
    ADMIN_USERNAME=admin
    ADMIN_PASSWORD=123456
    TOURIST_USERNAME=tourist
    TOURIST_PASSWORD=123456
    TIMEOUT=15
    REPORT_DIR=scripts
"""

from __future__ import annotations

import dataclasses
import json
import os
import sys
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Tuple

import pymysql
import requests


BASE_URL = os.environ.get("BASE_URL", "http://127.0.0.1:8137").rstrip("/")
DB_HOST = os.environ.get("DB_HOST", "127.0.0.1")
DB_PORT = int(os.environ.get("DB_PORT", "3306"))
DB_USER = os.environ.get("DB_USER", "root")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "hcc153502")
DB_NAME = os.environ.get("DB_NAME", "ai_guide_platform")
ADMIN_USERNAME = os.environ.get("ADMIN_USERNAME", "admin")
ADMIN_PASSWORD = os.environ.get("ADMIN_PASSWORD", "123456")
TOURIST_USERNAME = os.environ.get("TOURIST_USERNAME", "tourist")
TOURIST_PASSWORD = os.environ.get("TOURIST_PASSWORD", "123456")
TIMEOUT = int(os.environ.get("TIMEOUT", "15"))
REPORT_DIR = Path(os.environ.get("REPORT_DIR", Path(__file__).resolve().parent))
TMP_DIR = REPORT_DIR / "tmp"
REPORT_JSON = REPORT_DIR / "api_test_report.json"
REPORT_MD = REPORT_DIR / "api_test_report.md"


def now_stamp() -> str:
    return datetime.now().strftime("%Y%m%d_%H%M%S")


def pretty_json(value: Any, limit: int = 1600) -> str:
    try:
        text = json.dumps(value, ensure_ascii=False, indent=2, default=str)
    except Exception:
        text = str(value)
    if len(text) > limit:
        return text[:limit] + "..."
    return text


def body_preview(value: Any, limit: int = 260) -> str:
    try:
        text = json.dumps(value, ensure_ascii=False, default=str)
    except Exception:
        text = str(value)
    text = " ".join(text.split())
    if len(text) > limit:
        return text[:limit] + "..."
    return text


def safe_int(value: Any) -> Optional[int]:
    try:
        return int(value)
    except Exception:
        return None


def safe_data(resp_json: Any) -> Any:
    if isinstance(resp_json, dict) and "data" in resp_json:
        return resp_json.get("data")
    return resp_json


@dataclasses.dataclass
class RequestSpec:
    method: str
    path: str
    session: requests.Session
    params: Optional[Dict[str, Any]] = None
    json_body: Any = None
    files: Any = None
    data: Any = None
    headers: Optional[Dict[str, str]] = None


@dataclasses.dataclass
class CaseResult:
    name: str
    method: str
    path: str
    url: str
    status: str
    http_status: Optional[int]
    response_code: Optional[int]
    response_message: str
    response_body: Any
    request: Dict[str, Any]
    note: str = ""


class ApiSmokeTester:
    def __init__(self) -> None:
        self.results: List[CaseResult] = []
        self.context: Dict[str, Any] = {}
        self.report_dir = REPORT_DIR
        self.report_dir.mkdir(parents=True, exist_ok=True)
        TMP_DIR.mkdir(parents=True, exist_ok=True)

    # ------------------------------
    # DB bootstrap / login helpers
    # ------------------------------
    def login(self, username: str, password: str, label: str) -> requests.Session:
        session = requests.Session()
        spec = RequestSpec(
            method="POST",
            path="/api/auth/login",
            session=session,
            json_body={"username": username, "password": password},
        )
        result = self.send_request(label, spec)
        if result.response_code != 0:
            raise RuntimeError(f"{label} 登录失败: {result.response_message} | {body_preview(result.response_body)}")
        return session

    def record_skip(self, name: str, method: str, path: str, reason: str) -> None:
        result = CaseResult(
            name=name,
            method=method,
            path=path,
            url=BASE_URL + path,
            status="SKIP",
            http_status=None,
            response_code=None,
            response_message=reason,
            response_body={"skipped": True, "reason": reason},
            request={},
            note=reason,
        )
        self.results.append(result)
        self.print_result(result)

    # ------------------------------
    # HTTP helper
    # ------------------------------
    def send_request(
        self,
        name: str,
        spec: RequestSpec,
        expected_success: bool = True,
        log_stdout: bool = True,
        note: str = "",
    ) -> CaseResult:
        url = BASE_URL + spec.path
        method = spec.method.upper()
        req_headers = dict(spec.headers or {})
        response_body: Any = None
        http_status: Optional[int] = None
        response_code: Optional[int] = None
        response_message = ""
        status = "PASS"
        error_note = note

        try:
            resp = spec.session.request(
                method=method,
                url=url,
                params=spec.params,
                json=spec.json_body,
                data=spec.data,
                files=spec.files,
                headers=req_headers,
                timeout=TIMEOUT,
            )
            http_status = resp.status_code
            try:
                response_body = resp.json()
            except Exception:
                response_body = {"raw": resp.text}

            if isinstance(response_body, dict):
                response_code = safe_int(response_body.get("code"))
                response_message = str(response_body.get("message", ""))
            else:
                response_message = "non-json response"
                status = "WARN"

            if expected_success and response_code not in {0, None}:
                status = "FAIL"
            elif not expected_success and response_code == 0:
                status = "WARN"

        except Exception as exc:  # noqa: BLE001
            status = "ERROR"
            response_body = {"exception": type(exc).__name__, "message": str(exc)}
            response_message = str(exc)

        result = CaseResult(
            name=name,
            method=method,
            path=spec.path,
            url=url,
            status=status,
            http_status=http_status,
            response_code=response_code,
            response_message=response_message,
            response_body=response_body,
            request={
                "params": spec.params,
                "json": spec.json_body,
                "data": spec.data,
                "has_files": spec.files is not None,
                "headers": req_headers,
            },
            note=error_note,
        )
        self.results.append(result)

        if log_stdout:
            self.print_result(result)
        return result

    def print_result(self, result: CaseResult) -> None:
        label = f"[{result.status}] {result.method} {result.path}"
        if result.http_status is not None:
            label += f" http={result.http_status}"
        if result.response_code is not None:
            label += f" code={result.response_code}"
        if result.response_message:
            label += f" message={result.response_message}"
        print(label)
        print(f"  response: {body_preview(result.response_body)}")
        if result.note:
            print(f"  note: {result.note}")

    # ------------------------------
    # Data helpers
    # ------------------------------
    def api_get_data(self, session: requests.Session, path: str, params: Optional[Dict[str, Any]] = None) -> Any:
        resp = session.get(BASE_URL + path, params=params, timeout=TIMEOUT)
        try:
            body = resp.json()
        except Exception:
            raise RuntimeError(f"GET {path} returned non-json: {resp.text[:200]}")
        if body.get("code") != 0:
            raise RuntimeError(f"GET {path} failed: {body_preview(body)}")
        return body.get("data")

    def first_id(self, records: Iterable[Dict[str, Any]]) -> Optional[int]:
        for record in records:
            value = record.get("id")
            if value is not None:
                return int(value)
        return None

    def load_seed_ids(self, session: requests.Session) -> None:
        scenic_page = self.api_get_data(session, "/api/scenic/page", {"current": 1, "pageSize": 20})
        route_page = self.api_get_data(session, "/api/route/page", {"current": 1, "pageSize": 20})
        scenic_records = scenic_page.get("records", []) if isinstance(scenic_page, dict) else []
        route_records = route_page.get("records", []) if isinstance(route_page, dict) else []
        scenic_ids = [int(item["id"]) for item in scenic_records if item.get("id") is not None]
        route_ids = [int(item["id"]) for item in route_records if item.get("id") is not None]

        self.context["scenic_ids"] = scenic_ids or [1, 2, 3]
        self.context["route_ids"] = route_ids or [1, 2, 3]
        self.context["category_ids"] = [1, 2, 3]
        self.context["primary_scenic_id"] = self.context["scenic_ids"][0]
        self.context["secondary_scenic_id"] = self.context["scenic_ids"][1] if len(self.context["scenic_ids"]) > 1 else self.context["scenic_ids"][0]
        self.context["primary_route_id"] = self.context["route_ids"][0]

    # ------------------------------
    # Test cases
    # ------------------------------
    def run(self) -> None:
        print(f"🚀 API smoke tester starting at {BASE_URL}")
        tourist = self.try_login_or_none(TOURIST_USERNAME, TOURIST_PASSWORD, "AUTH_LOGIN_TOURIST")
        admin = self.try_login_or_none(ADMIN_USERNAME, ADMIN_PASSWORD, "AUTH_LOGIN_ADMIN")
        self.context["tourist_session"] = tourist
        self.context["admin_session"] = admin

        # Use whichever authenticated session is available to fetch seeded IDs.
        seed_session = admin or tourist
        if seed_session is not None:
            self.load_seed_ids(seed_session)
        else:
            self.context["scenic_ids"] = [1, 2, 3]
            self.context["route_ids"] = [1, 2, 3]
            self.context["category_ids"] = [1, 2, 3]
            self.context["primary_scenic_id"] = 1
            self.context["secondary_scenic_id"] = 2
            self.context["primary_route_id"] = 1

        self.run_core_public_cases()
        self.run_user_cases()
        self.run_favorite_cases()
        self.run_feedback_cases()
        self.run_ai_cases()
        self.run_file_cases()
        self.run_admin_cases()
        self.run_stateful_crud_cases()
        self.run_logout_cases()

        self.write_reports()
        self.print_summary()

    def try_login_or_none(self, username: str, password: str, label: str) -> Optional[requests.Session]:
        try:
            return self.login(username, password, label)
        except Exception as exc:  # noqa: BLE001
            print(f"⚠️  {label} failed: {exc}")
            return None

    def run_core_public_cases(self) -> None:
        scenic_id = self.context["primary_scenic_id"]
        route_id = self.context["primary_route_id"]
        anon = requests.Session()

        cases = [
            ("SCENIC_CATEGORY_LIST", RequestSpec("GET", "/api/scenic/category/list", anon), True),
            ("SCENIC_PAGE", RequestSpec("GET", "/api/scenic/page", anon, params={"current": 1, "pageSize": 5, "keyword": "北京"}), True),
            ("SCENIC_DETAIL", RequestSpec("GET", "/api/scenic/detail", anon, params={"id": scenic_id}), True),
            ("SCENIC_SEARCH", RequestSpec("GET", "/api/scenic/search", anon, params={"current": 1, "pageSize": 5, "keyword": "湖"}), True),
            ("SCENIC_I18N_DETAIL", RequestSpec("GET", "/api/scenic/i18n/detail", anon, params={"scenicSpotId": scenic_id}), True),
            ("SCENIC_I18N_LANG", RequestSpec("GET", "/api/scenic/i18n/lang", anon, params={"scenicSpotId": scenic_id, "languageCode": "en-US"}), True),
            ("SCENIC_MEDIA_LIST", RequestSpec("GET", "/api/scenic/media/list", anon, params={"scenicSpotId": scenic_id}), True),
            ("ROUTE_PAGE", RequestSpec("GET", "/api/route/page", anon, params={"current": 1, "pageSize": 5, "theme": "历史文化"}), True),
            ("ROUTE_DETAIL", RequestSpec("GET", "/api/route/detail", anon, params={"id": route_id}), True),
            ("ROUTE_RECOMMEND", RequestSpec("GET", "/api/route/recommend", anon, params={"theme": "自然风光", "suitableCrowd": "情侣", "limit": 3}), True),
            ("ROUTE_I18N_DETAIL", RequestSpec("GET", "/api/route/i18n/detail", anon, params={"routeId": route_id}), True),
        ]
        for name, spec, expected_success in cases:
            self.send_request(name, spec, expected_success=expected_success)

    def run_user_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        if tourist is None:
            self.record_skip("AUTH_ME", "GET", "/api/auth/me", "tourist login unavailable")
            self.record_skip("USER_PROFILE", "GET", "/api/user/profile", "tourist login unavailable")
            self.record_skip("USER_UPDATE_PROFILE", "POST", "/api/user/update", "tourist login unavailable")
            self.record_skip("USER_CHANGE_PASSWORD", "POST", "/api/user/change-password", "tourist login unavailable")
            self.record_skip("USER_PROFILE_AFTER_RESTORE", "GET", "/api/user/profile", "tourist login unavailable")
            return
        original_password = TOURIST_PASSWORD
        temp_password = "TempPass123!"

        self.send_request(
            "USER_PROFILE",
            RequestSpec("GET", "/api/user/profile", tourist),
        )
        self.send_request(
            "USER_UPDATE_PROFILE",
            RequestSpec(
                "POST",
                "/api/user/update",
                tourist,
                json_body={
                    "nickname": f"游客_{now_stamp()}",
                    "email": "tourist+api@test.com",
                    "phone": "13800009999",
                },
            ),
        )
        self.send_request(
            "USER_CHANGE_PASSWORD",
            RequestSpec(
                "POST",
                "/api/user/change-password",
                tourist,
                json_body={"oldPassword": original_password, "newPassword": temp_password},
            ),
        )
        # Re-login with the new password to validate the endpoint really changed the credential.
        temp_login = self.login(TOURIST_USERNAME, temp_password, "AUTH_LOGIN_TOURIST_AFTER_CHANGE")
        self.context["tourist_session_after_password_change"] = temp_login
        # Restore the password so later re-runs keep using the same credentials.
        self.send_request(
            "USER_RESTORE_PASSWORD",
            RequestSpec(
                "POST",
                "/api/user/change-password",
                temp_login,
                json_body={"oldPassword": temp_password, "newPassword": original_password},
            ),
        )
        self.send_request(
            "USER_PROFILE_AFTER_RESTORE",
            RequestSpec("GET", "/api/user/profile", temp_login),
        )

    def run_favorite_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        if tourist is None:
            self.record_skip("FAVORITE_CHECK_SCENIC", "GET", "/api/favorite/check", "tourist login unavailable")
            self.record_skip("FAVORITE_ADD_SCENIC", "POST", "/api/favorite/add", "tourist login unavailable")
            self.record_skip("FAVORITE_PAGE", "GET", "/api/favorite/page", "tourist login unavailable")
            self.record_skip("FAVORITE_CANCEL_SCENIC", "POST", "/api/favorite/cancel", "tourist login unavailable")
            self.record_skip("FAVORITE_CHECK_ROUTE", "GET", "/api/favorite/check", "tourist login unavailable")
            self.record_skip("FAVORITE_ADD_ROUTE", "POST", "/api/favorite/add", "tourist login unavailable")
            self.record_skip("FAVORITE_CANCEL_ROUTE", "POST", "/api/favorite/cancel", "tourist login unavailable")
            return
        scenic_id = self.context["secondary_scenic_id"]
        route_id = self.context["primary_route_id"]

        self.send_request(
            "FAVORITE_CHECK_SCENIC",
            RequestSpec("GET", "/api/favorite/check", tourist, params={"bizType": "SCENIC", "bizId": scenic_id}),
        )
        self.send_request(
            "FAVORITE_ADD_SCENIC",
            RequestSpec("POST", "/api/favorite/add", tourist, json_body={"bizType": "SCENIC", "bizId": scenic_id}),
        )
        self.send_request(
            "FAVORITE_PAGE",
            RequestSpec("GET", "/api/favorite/page", tourist, params={"current": 1, "pageSize": 10, "bizType": "SCENIC"}),
        )
        self.send_request(
            "FAVORITE_CANCEL_SCENIC",
            RequestSpec("POST", "/api/favorite/cancel", tourist, json_body={"bizType": "SCENIC", "bizId": scenic_id}),
        )
        self.send_request(
            "FAVORITE_CHECK_ROUTE",
            RequestSpec("GET", "/api/favorite/check", tourist, params={"bizType": "ROUTE", "bizId": route_id}),
        )
        self.send_request(
            "FAVORITE_ADD_ROUTE",
            RequestSpec("POST", "/api/favorite/add", tourist, json_body={"bizType": "ROUTE", "bizId": route_id}),
        )
        self.send_request(
            "FAVORITE_CANCEL_ROUTE",
            RequestSpec("POST", "/api/favorite/cancel", tourist, json_body={"bizType": "ROUTE", "bizId": route_id}),
        )

    def run_feedback_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        admin = self.context.get("admin_session")
        if tourist is None:
            self.record_skip("FEEDBACK_CREATE", "POST", "/api/feedback/create", "tourist login unavailable")
            self.record_skip("FEEDBACK_MY_PAGE", "GET", "/api/feedback/my/page", "tourist login unavailable")
        if admin is None:
            self.record_skip("ADMIN_FEEDBACK_PAGE", "GET", "/api/admin/feedback/page", "admin login unavailable")
            self.record_skip("ADMIN_FEEDBACK_REPLY", "POST", "/api/admin/feedback/reply", "admin login unavailable")
            return

        create_result = self.send_request(
            "FEEDBACK_CREATE",
            RequestSpec(
                "POST",
                "/api/feedback/create",
                tourist,
                json_body={
                    "feedbackType": "BUG",
                    "content": f"接口自动化测试反馈 {now_stamp()}",
                    "contactInfo": "tourist+api@test.com",
                },
            ),
        )

        self.send_request(
            "FEEDBACK_MY_PAGE",
            RequestSpec("GET", "/api/feedback/my/page", tourist, params={"current": 1, "pageSize": 10}),
        )
        self.send_request(
            "ADMIN_FEEDBACK_PAGE",
            RequestSpec("GET", "/api/admin/feedback/page", admin, params={"current": 1, "pageSize": 10}),
        )
        feedback_id = self.extract_created_id(create_result)
        if feedback_id is not None:
            self.send_request(
                "ADMIN_FEEDBACK_REPLY",
                RequestSpec(
                    "POST",
                    "/api/admin/feedback/reply",
                    admin,
                    json_body={
                        "id": feedback_id,
                        "replyContent": "收到，已记录并进入自动化回归验证。",
                        "feedbackStatus": 1,
                    },
                ),
            )

    def run_ai_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        if tourist is None:
            self.record_skip("AI_EXPLAIN_SCENIC", "POST", "/api/ai/guide/explain-scenic", "tourist login unavailable")
            self.record_skip("AI_COMPARE_SCENIC", "POST", "/api/ai/guide/compare-scenic", "tourist login unavailable")
            self.record_skip("AI_RECOMMEND_ROUTE", "POST", "/api/ai/guide/recommend-route", "tourist login unavailable")
            self.record_skip("AI_COMMON_QUESTION", "POST", "/api/ai/guide/common-question", "tourist login unavailable")
            self.record_skip("AI_TRANSLATE_ANSWER", "POST", "/api/ai/guide/translate-answer", "tourist login unavailable")
            return
        scenic_id = self.context["primary_scenic_id"]
        scenic_id_b = self.context["secondary_scenic_id"]
        route_id = self.context["primary_route_id"]

        self.send_request(
            "AI_EXPLAIN_SCENIC",
            RequestSpec(
                "POST",
                "/api/ai/guide/explain-scenic",
                tourist,
                json_body={
                    "question": "请详细讲解这个景点的历史和看点",
                    "scenicSpotId": scenic_id,
                    "routeId": route_id,
                    "languageCode": "zh-CN",
                },
            ),
        )
        self.send_request(
            "AI_COMPARE_SCENIC",
            RequestSpec(
                "POST",
                "/api/ai/guide/compare-scenic",
                tourist,
                json_body={
                    "scenicSpotIdA": scenic_id,
                    "scenicSpotIdB": scenic_id_b,
                    "languageCode": "zh-CN",
                },
            ),
        )
        self.send_request(
            "AI_RECOMMEND_ROUTE",
            RequestSpec(
                "POST",
                "/api/ai/guide/recommend-route",
                tourist,
                json_body={
                    "timeBudget": "1-2天",
                    "interest": "历史文化",
                    "crowd": "家庭",
                    "languageCode": "zh-CN",
                },
            ),
        )
        self.send_request(
            "AI_COMMON_QUESTION",
            RequestSpec(
                "POST",
                "/api/ai/guide/common-question",
                tourist,
                json_body={
                    "question": "请告诉我景点参观时有哪些注意事项",
                    "scenicSpotId": scenic_id,
                    "languageCode": "zh-CN",
                },
            ),
        )
        self.send_request(
            "AI_TRANSLATE_ANSWER",
            RequestSpec(
                "POST",
                "/api/ai/guide/translate-answer",
                tourist,
                json_body={
                    "question": "Please translate this travel explanation into English.",
                    "scenicSpotId": scenic_id,
                    "languageCode": "en-US",
                },
            ),
        )

    def tiny_png(self) -> bytes:
        # 1x1 transparent PNG
        return bytes.fromhex(
            "89504e470d0a1a0a0000000d49484452000000010000000108060000001f15c489"
            "0000000a49444154789c6360000002000154a24f5d0000000049454e44ae426082"
        )

    def run_file_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        if tourist is None:
            self.record_skip("FILE_UPLOAD", "POST", "/api/file/upload", "tourist login unavailable")
            return
        files = {"file": ("api-test.png", self.tiny_png(), "image/png")}
        self.send_request(
            "FILE_UPLOAD",
            RequestSpec(
                "POST",
                "/api/file/upload",
                tourist,
                files=files,
                data={"bizType": "api_test"},
            ),
        )

    def run_admin_cases(self) -> None:
        admin = self.context.get("admin_session")
        if admin is None:
            self.record_skip("ADMIN_DASHBOARD", "GET", "/api/admin/statistics/dashboard", "admin login unavailable")
            self.record_skip("ADMIN_USER_PAGE", "GET", "/api/admin/user/page", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_CATEGORY_PAGE", "GET", "/api/admin/scenic/category/page", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_PAGE", "GET", "/api/admin/scenic/page", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_DETAIL", "GET", "/api/admin/scenic/detail", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_I18N_LIST", "GET", "/api/admin/scenic/i18n/list", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_MEDIA_LIST", "GET", "/api/admin/scenic/media/list", "admin login unavailable")
            self.record_skip("ADMIN_ROUTE_PAGE", "GET", "/api/admin/route/page", "admin login unavailable")
            self.record_skip("ADMIN_ROUTE_DETAIL", "GET", "/api/admin/route/detail", "admin login unavailable")
            self.record_skip("ADMIN_ROUTE_I18N_LIST", "GET", "/api/admin/route/i18n/list", "admin login unavailable")
            self.record_skip("ADMIN_FEEDBACK_PAGE_AGAIN", "GET", "/api/admin/feedback/page", "admin login unavailable")
            self.record_skip("ADMIN_AI_LOG_PAGE", "GET", "/api/admin/ai/log/page", "admin login unavailable")
            self.record_skip("ADMIN_FILE_PAGE", "GET", "/api/admin/file/page", "admin login unavailable")
            return
        tourist_user_id = self.query_user_id("tourist")

        self.send_request(
            "ADMIN_DASHBOARD",
            RequestSpec("GET", "/api/admin/statistics/dashboard", admin),
        )
        self.send_request(
            "ADMIN_USER_PAGE",
            RequestSpec("GET", "/api/admin/user/page", admin, params={"current": 1, "pageSize": 10}),
        )
        if tourist_user_id is not None:
            # Toggle a normal user status and restore it immediately.
            self.send_request(
                "ADMIN_USER_STATUS_DISABLE",
                RequestSpec("POST", "/api/admin/user/status", admin, json_body={"id": tourist_user_id, "userStatus": 0}),
            )
            self.send_request(
                "ADMIN_USER_STATUS_ENABLE",
                RequestSpec("POST", "/api/admin/user/status", admin, json_body={"id": tourist_user_id, "userStatus": 1}),
            )

        self.send_request(
            "ADMIN_SCENIC_CATEGORY_PAGE",
            RequestSpec("GET", "/api/admin/scenic/category/page", admin, params={"current": 1, "pageSize": 20}),
        )
        self.send_request(
            "ADMIN_SCENIC_PAGE",
            RequestSpec("GET", "/api/admin/scenic/page", admin, params={"current": 1, "pageSize": 20}),
        )
        self.send_request(
            "ADMIN_SCENIC_DETAIL",
            RequestSpec("GET", "/api/admin/scenic/detail", admin, params={"id": self.context["primary_scenic_id"]}),
        )
        self.send_request(
            "ADMIN_SCENIC_I18N_LIST",
            RequestSpec("GET", "/api/admin/scenic/i18n/list", admin, params={"scenicSpotId": self.context["primary_scenic_id"]}),
        )
        self.send_request(
            "ADMIN_SCENIC_MEDIA_LIST",
            RequestSpec("GET", "/api/admin/scenic/media/list", admin, params={"scenicSpotId": self.context["primary_scenic_id"]}),
        )
        self.send_request(
            "ADMIN_ROUTE_PAGE",
            RequestSpec("GET", "/api/admin/route/page", admin, params={"current": 1, "pageSize": 20}),
        )
        self.send_request(
            "ADMIN_ROUTE_DETAIL",
            RequestSpec("GET", "/api/admin/route/detail", admin, params={"id": self.context["primary_route_id"]}),
        )
        self.send_request(
            "ADMIN_ROUTE_I18N_LIST",
            RequestSpec("GET", "/api/admin/route/i18n/list", admin, params={"routeId": self.context["primary_route_id"]}),
        )
        self.send_request(
            "ADMIN_FEEDBACK_PAGE_AGAIN",
            RequestSpec("GET", "/api/admin/feedback/page", admin, params={"current": 1, "pageSize": 20}),
        )
        self.send_request(
            "ADMIN_AI_LOG_PAGE",
            RequestSpec("GET", "/api/admin/ai/log/page", admin, params={"current": 1, "pageSize": 20}),
        )
        self.send_request(
            "ADMIN_FILE_PAGE",
            RequestSpec("GET", "/api/admin/file/page", admin, params={"current": 1, "pageSize": 20}),
        )

    def run_stateful_crud_cases(self) -> None:
        admin = self.context.get("admin_session")
        if admin is None:
            self.record_skip("ADMIN_SCENIC_CATEGORY_SAVE_CREATE", "POST", "/api/admin/scenic/category/save", "admin login unavailable")
            self.record_skip("ADMIN_SCENIC_SAVE_CREATE", "POST", "/api/admin/scenic/save", "admin login unavailable")
            self.record_skip("ADMIN_ROUTE_SAVE_CREATE", "POST", "/api/admin/route/save", "admin login unavailable")
            return
        scenic_category_id = self.create_temp_category(admin)
        scenic_spot_id = self.create_temp_scenic_spot(admin, scenic_category_id)
        route_id = self.create_temp_route(admin)

        if scenic_category_id is not None:
            self.send_request(
                "ADMIN_SCENIC_CATEGORY_SAVE_UPDATE",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/category/save",
                    admin,
                    json_body={
                        "id": scenic_category_id,
                        "categoryName": f"测试分类更新-{now_stamp()}",
                        "categoryDesc": "自动化测试更新后的分类描述",
                        "sortNo": 99,
                    },
                ),
            )
            self.send_request(
                "ADMIN_SCENIC_CATEGORY_STATUS",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/category/status",
                    admin,
                    json_body={"id": scenic_category_id, "categoryStatus": 0},
                ),
            )
            self.send_request(
                "ADMIN_SCENIC_CATEGORY_STATUS_RESTORE",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/category/status",
                    admin,
                    json_body={"id": scenic_category_id, "categoryStatus": 1},
                ),
            )
            self.send_request(
                "ADMIN_SCENIC_CATEGORY_DELETE",
                RequestSpec("POST", "/api/admin/scenic/category/delete", admin, json_body={"id": scenic_category_id}),
            )

        if scenic_spot_id is not None:
            self.send_request(
                "ADMIN_SCENIC_SAVE_UPDATE",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/save",
                    admin,
                    json_body={
                        "id": scenic_spot_id,
                        "categoryId": 1,
                        "spotName": f"测试景点更新-{now_stamp()}",
                        "city": "杭州",
                        "address": "浙江省杭州市西湖区测试路 1 号",
                        "longitude": 120.12,
                        "latitude": 30.28,
                        "coverUrl": "https://example.com/test-cover.jpg",
                        "summary": "更新后的测试景点摘要",
                        "description": "这是自动化测试脚本创建并更新的景点。",
                        "openTime": "全天开放",
                        "suggestDuration": "2小时",
                        "tips": "仅用于接口测试",
                    },
                ),
            )
            self.send_request(
                "ADMIN_SCENIC_STATUS",
                RequestSpec("POST", "/api/admin/scenic/status", admin, params={"id": scenic_spot_id, "status": 0}),
            )
            self.send_request(
                "ADMIN_SCENIC_STATUS_RESTORE",
                RequestSpec("POST", "/api/admin/scenic/status", admin, params={"id": scenic_spot_id, "status": 1}),
            )
            i18n_create = self.send_request(
                "ADMIN_SCENIC_I18N_SAVE_CREATE",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/i18n/save",
                    admin,
                    json_body={
                        "scenicSpotId": scenic_spot_id,
                        "languageCode": "fr-FR",
                        "title": "Test Scenic Spot",
                        "summary": "Created by API smoke test",
                        "description": "Temporary multilingual entry for testing.",
                        "tips": "Temporary record",
                    },
                ),
            )
            scenic_i18n_id = self.extract_created_id(i18n_create)
            if scenic_i18n_id is not None:
                self.send_request(
                    "ADMIN_SCENIC_I18N_SAVE_UPDATE",
                    RequestSpec(
                        "POST",
                        "/api/admin/scenic/i18n/save",
                        admin,
                        json_body={
                            "id": scenic_i18n_id,
                            "scenicSpotId": scenic_spot_id,
                            "languageCode": "fr-FR",
                            "title": "Test Scenic Spot Updated",
                            "summary": "Updated multilingual entry",
                            "description": "Updated temporary multilingual entry.",
                            "tips": "Still temporary",
                        },
                    ),
                )
            media_create = self.send_request(
                "ADMIN_SCENIC_MEDIA_ADD",
                RequestSpec(
                    "POST",
                    "/api/admin/scenic/media/add",
                    admin,
                    json_body={
                        "scenicSpotId": scenic_spot_id,
                        "mediaType": "image",
                        "mediaUrl": "https://example.com/test-media.jpg",
                        "mediaName": "test-media",
                    },
                ),
            )
            media_id = self.extract_created_id(media_create)
            if media_id is not None:
                self.send_request(
                    "ADMIN_SCENIC_MEDIA_DELETE",
                    RequestSpec("POST", "/api/admin/scenic/media/delete", admin, json_body={"id": media_id}),
                )
            self.send_request(
                "ADMIN_SCENIC_I18N_LIST_AFTER_CREATE",
                RequestSpec("GET", "/api/admin/scenic/i18n/list", admin, params={"scenicSpotId": scenic_spot_id}),
            )
            self.send_request(
                "ADMIN_SCENIC_MEDIA_LIST_AFTER_CREATE",
                RequestSpec("GET", "/api/admin/scenic/media/list", admin, params={"scenicSpotId": scenic_spot_id}),
            )
            self.send_request(
                "ADMIN_SCENIC_DELETE",
                RequestSpec("POST", "/api/admin/scenic/delete", admin, json_body={"id": scenic_spot_id}),
            )

        if route_id is not None:
            self.send_request(
                "ADMIN_ROUTE_SAVE_UPDATE",
                RequestSpec(
                    "POST",
                    "/api/admin/route/save",
                    admin,
                    json_body={
                        "id": route_id,
                        "routeName": f"测试路线更新-{now_stamp()}",
                        "theme": "历史文化",
                        "coverUrl": "https://example.com/route-cover.jpg",
                        "summary": "更新后的路线摘要",
                        "description": "这是自动化测试脚本创建并更新的路线。",
                        "suggestDuration": "1天",
                        "suitableCrowd": "家庭",
                        "spots": [
                            {"scenicSpotId": 1, "sortNo": 1, "stayDuration": "2小时"},
                            {"scenicSpotId": 2, "sortNo": 2, "stayDuration": "2小时"},
                        ],
                    },
                ),
            )
            self.send_request(
                "ADMIN_ROUTE_STATUS",
                RequestSpec("POST", "/api/admin/route/status", admin, params={"id": route_id, "status": 0}),
            )
            self.send_request(
                "ADMIN_ROUTE_STATUS_RESTORE",
                RequestSpec("POST", "/api/admin/route/status", admin, params={"id": route_id, "status": 1}),
            )
            route_i18n_create = self.send_request(
                "ADMIN_ROUTE_I18N_SAVE_CREATE",
                RequestSpec(
                    "POST",
                    "/api/admin/route/i18n/save",
                    admin,
                    json_body={
                        "routeId": route_id,
                        "languageCode": "fr-FR",
                        "title": "Test Route",
                        "summary": "Created by API smoke test",
                        "description": "Temporary multilingual route content for testing.",
                        "travelTips": "Temporary record",
                    },
                ),
            )
            route_i18n_id = self.extract_created_id(route_i18n_create)
            if route_i18n_id is not None:
                self.send_request(
                    "ADMIN_ROUTE_I18N_SAVE_UPDATE",
                    RequestSpec(
                        "POST",
                        "/api/admin/route/i18n/save",
                        admin,
                        json_body={
                            "id": route_i18n_id,
                            "routeId": route_id,
                            "languageCode": "fr-FR",
                            "title": "Test Route Updated",
                            "summary": "Updated temporary route multilingual content",
                            "description": "Updated temporary multilingual route content.",
                            "travelTips": "Still temporary",
                        },
                    ),
                )
            self.send_request(
                "ADMIN_ROUTE_I18N_LIST_AFTER_CREATE",
                RequestSpec("GET", "/api/admin/route/i18n/list", admin, params={"routeId": route_id}),
            )
            self.send_request(
                "ADMIN_ROUTE_DELETE",
                RequestSpec("POST", "/api/admin/route/delete", admin, json_body={"id": route_id}),
            )

    def create_temp_category(self, admin: requests.Session) -> Optional[int]:
        result = self.send_request(
            "ADMIN_SCENIC_CATEGORY_SAVE_CREATE",
            RequestSpec(
                "POST",
                "/api/admin/scenic/category/save",
                admin,
                json_body={
                    "categoryName": f"测试分类-{now_stamp()}",
                    "categoryDesc": "仅用于自动化测试",
                    "sortNo": 99,
                },
            ),
        )
        return self.extract_created_id(result)

    def create_temp_scenic_spot(self, admin: requests.Session, category_id: Optional[int]) -> Optional[int]:
        if category_id is None:
            category_id = 1
        result = self.send_request(
            "ADMIN_SCENIC_SAVE_CREATE",
            RequestSpec(
                "POST",
                "/api/admin/scenic/save",
                admin,
                json_body={
                    "categoryId": category_id,
                    "spotName": f"测试景点-{now_stamp()}",
                    "city": "杭州",
                    "address": "浙江省杭州市西湖区测试路 1 号",
                    "longitude": 120.12,
                    "latitude": 30.28,
                    "coverUrl": "https://example.com/test-cover.jpg",
                    "summary": "用于自动化测试的景点摘要",
                    "description": "这是自动化测试脚本创建的景点。",
                    "openTime": "全天开放",
                    "suggestDuration": "2小时",
                    "tips": "仅用于接口测试",
                },
            ),
        )
        return self.extract_created_id(result)

    def create_temp_route(self, admin: requests.Session) -> Optional[int]:
        result = self.send_request(
            "ADMIN_ROUTE_SAVE_CREATE",
            RequestSpec(
                "POST",
                "/api/admin/route/save",
                admin,
                json_body={
                    "routeName": f"测试路线-{now_stamp()}",
                    "theme": "历史文化",
                    "coverUrl": "https://example.com/route-cover.jpg",
                    "summary": "用于自动化测试的路线摘要",
                    "description": "这是自动化测试脚本创建的路线。",
                    "suggestDuration": "1天",
                    "suitableCrowd": "家庭",
                    "spots": [
                        {"scenicSpotId": 1, "sortNo": 1, "stayDuration": "2小时"},
                        {"scenicSpotId": 2, "sortNo": 2, "stayDuration": "2小时"},
                    ],
                },
            ),
        )
        return self.extract_created_id(result)

    def run_logout_cases(self) -> None:
        tourist = self.context.get("tourist_session")
        admin = self.context.get("admin_session")

        if tourist is not None:
            self.send_request("AUTH_LOGOUT_TOURIST", RequestSpec("POST", "/api/auth/logout", tourist))
            self.send_request("AUTH_ME_AFTER_TOURIST_LOGOUT", RequestSpec("GET", "/api/auth/me", tourist))
        else:
            self.record_skip("AUTH_LOGOUT_TOURIST", "POST", "/api/auth/logout", "tourist login unavailable")
            self.record_skip("AUTH_ME_AFTER_TOURIST_LOGOUT", "GET", "/api/auth/me", "tourist login unavailable")

        if admin is not None:
            self.send_request("AUTH_LOGOUT_ADMIN", RequestSpec("POST", "/api/auth/logout", admin))
            self.send_request("AUTH_ME_AFTER_ADMIN_LOGOUT", RequestSpec("GET", "/api/auth/me", admin))
        else:
            self.record_skip("AUTH_LOGOUT_ADMIN", "POST", "/api/auth/logout", "admin login unavailable")
            self.record_skip("AUTH_ME_AFTER_ADMIN_LOGOUT", "GET", "/api/auth/me", "admin login unavailable")

    # ------------------------------
    # Misc
    # ------------------------------
    def query_user_id(self, username: str) -> Optional[int]:
        conn = pymysql.connect(
            host=DB_HOST,
            port=DB_PORT,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            charset="utf8mb4",
        )
        try:
            with conn.cursor() as cur:
                cur.execute("SELECT id FROM sys_user WHERE username = %s LIMIT 1", (username,))
                row = cur.fetchone()
                if not row:
                    return None
                return int(row[0])
        finally:
            conn.close()

    def extract_created_id(self, result: CaseResult) -> Optional[int]:
        body = result.response_body
        if isinstance(body, dict):
            data = body.get("data")
            if isinstance(data, int):
                return data
            if isinstance(data, str) and data.isdigit():
                return int(data)
            if isinstance(data, dict):
                for key in ("id", "data", "value"):
                    value = data.get(key)
                    if isinstance(value, int):
                        return value
                    if isinstance(value, str) and value.isdigit():
                        return int(value)
        return None

    def write_reports(self) -> None:
        payload = {
            "baseUrl": BASE_URL,
            "generatedAt": datetime.now().isoformat(timespec="seconds"),
            "total": len(self.results),
            "passed": sum(1 for r in self.results if r.status == "PASS"),
            "warn": sum(1 for r in self.results if r.status == "WARN"),
            "skip": sum(1 for r in self.results if r.status == "SKIP"),
            "error": sum(1 for r in self.results if r.status in {"FAIL", "ERROR"}),
            "results": [dataclasses.asdict(r) for r in self.results],
        }
        REPORT_JSON.write_text(json.dumps(payload, ensure_ascii=False, indent=2, default=str), encoding="utf-8")

        md_lines = [
            "# API Test Report",
            "",
            f"- Base URL: `{BASE_URL}`",
            f"- Generated At: `{payload['generatedAt']}`",
            f"- Total: `{payload['total']}`",
            f"- Passed: `{payload['passed']}`",
            f"- Warn: `{payload['warn']}`",
            f"- Skip: `{payload['skip']}`",
            f"- Error: `{payload['error']}`",
            "",
            "| # | Status | Method | Path | HTTP | Code | Message | Response Preview |",
            "|---:|:------:|:------:|:-----|-----:|-----:|:--------|:-----------------|",
        ]
        for idx, result in enumerate(self.results, 1):
            md_lines.append(
                "| {idx} | {status} | {method} | `{path}` | {http} | {code} | {msg} | {preview} |".format(
                    idx=idx,
                    status=result.status,
                    method=result.method,
                    path=result.path,
                    http="" if result.http_status is None else result.http_status,
                    code="" if result.response_code is None else result.response_code,
                    msg=str(result.response_message).replace("|", "\\|"),
                    preview=body_preview(result.response_body, limit=120).replace("|", "\\|"),
                )
            )
        REPORT_MD.write_text("\n".join(md_lines), encoding="utf-8")

        print(f"📄 JSON report: {REPORT_JSON}")
        print(f"📄 Markdown report: {REPORT_MD}")

    def print_summary(self) -> None:
        passed = sum(1 for r in self.results if r.status == "PASS")
        warn = sum(1 for r in self.results if r.status == "WARN")
        skip = sum(1 for r in self.results if r.status == "SKIP")
        error = sum(1 for r in self.results if r.status in {"FAIL", "ERROR"})
        print("\n=== Summary ===")
        print(f"Total: {len(self.results)}  Passed: {passed}  Warn: {warn}  Skip: {skip}  Error: {error}")
        if error:
            print("Some endpoints returned non-success responses. Check the report files for full payloads.")


def main() -> int:
    tester = ApiSmokeTester()
    try:
        tester.run()
        return 0
    except Exception as exc:  # noqa: BLE001
        print(f"\n❌ Fatal error: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
