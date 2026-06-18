# API Test Report

- Base URL: `http://127.0.0.1:8137`
- Generated At: `2026-06-16T11:00:47`
- Total: `79`
- Passed: `77`
- Warn: `0`
- Skip: `0`
- Error: `2`

| # | Status | Method | Path | HTTP | Code | Message | Response Preview |
|---:|:------:|:------:|:-----|-----:|-----:|:--------|:-----------------|
| 1 | PASS | POST | `/api/auth/login` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 2, "username": "tourist", "nickname": "游客_20260616_104550", "avatarUrl": nul... |
| 2 | PASS | POST | `/api/auth/login` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "username": "admin", "nickname": "管理员", "avatarUrl": null, "roleCode": "A... |
| 3 | PASS | GET | `/api/scenic/category/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "categoryName": "自然风光", "categoryDesc": "山水、湖泊、森林等自然景观", "sortNo": 1, "c... |
| 4 | PASS | GET | `/api/scenic/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 5, "total": 1, "records": [{"id": 1, "categoryId": 2, "c... |
| 5 | PASS | GET | `/api/scenic/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "categoryId": 2, "categoryName": "历史人文", "spotName": "故宫博物院", "city": "北京... |
| 6 | PASS | GET | `/api/scenic/search` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 5, "total": 1, "records": [{"id": 5, "categoryId": 2, "c... |
| 7 | PASS | GET | `/api/scenic/i18n/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "scenicSpotId": 1, "languageCode": "en-US", "title": "The Forbidden City... |
| 8 | PASS | GET | `/api/scenic/i18n/lang` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "scenicSpotId": 1, "languageCode": "en-US", "title": "The Forbidden City"... |
| 9 | PASS | GET | `/api/scenic/media/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "scenicSpotId": 1, "mediaType": "image", "mediaUrl": "https://communityf... |
| 10 | PASS | GET | `/api/route/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 5, "total": 2, "records": [{"id": 1, "routeName": "北京文化深... |
| 11 | PASS | GET | `/api/route/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "routeName": "北京文化深度游", "theme": "历史文化", "coverUrl": null, "summary": "探索... |
| 12 | PASS | GET | `/api/route/recommend` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 3, "routeName": "杭州诗意之旅", "theme": "自然风光", "coverUrl": null, "summary": "上有... |
| 13 | PASS | GET | `/api/route/i18n/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "routeId": 1, "languageCode": "en-US", "title": "Beijing Cultural Deep T... |
| 14 | PASS | GET | `/api/user/profile` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 2, "username": "tourist", "nickname": "游客_20260616_104550", "avatarUrl": nul... |
| 15 | PASS | POST | `/api/user/update` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 16 | PASS | POST | `/api/user/change-password` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 17 | PASS | POST | `/api/auth/login` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 2, "username": "tourist", "nickname": "游客_20260616_110019", "avatarUrl": nul... |
| 18 | PASS | POST | `/api/user/change-password` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 19 | PASS | GET | `/api/user/profile` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 2, "username": "tourist", "nickname": "游客_20260616_110019", "avatarUrl": nul... |
| 20 | PASS | GET | `/api/favorite/check` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": false} |
| 21 | FAIL | POST | `/api/favorite/add` | 200 | 50000 | 系统异常 | {"code": 50000, "message": "系统异常", "data": null} |
| 22 | PASS | GET | `/api/favorite/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 10, "total": 2, "records": [{"id": 1, "bizType": "SCENIC... |
| 23 | PASS | POST | `/api/favorite/cancel` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 24 | PASS | GET | `/api/favorite/check` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": false} |
| 25 | FAIL | POST | `/api/favorite/add` | 200 | 50000 | 系统异常 | {"code": 50000, "message": "系统异常", "data": null} |
| 26 | PASS | POST | `/api/favorite/cancel` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 27 | PASS | POST | `/api/feedback/create` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 5} |
| 28 | PASS | GET | `/api/feedback/my/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 10, "total": 3, "records": [{"id": 5, "userId": 2, "user... |
| 29 | PASS | GET | `/api/admin/feedback/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 10, "total": 5, "records": [{"id": 5, "userId": 2, "user... |
| 30 | PASS | POST | `/api/admin/feedback/reply` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 31 | PASS | POST | `/api/ai/guide/explain-scenic` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"answer": "AI服务暂时不可用，请稍后重试。", "languageCode": "zh-CN", "costMillis": 12148}} |
| 32 | PASS | POST | `/api/ai/guide/compare-scenic` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"answer": null, "languageCode": "zh-CN", "costMillis": 4602}} |
| 33 | PASS | POST | `/api/ai/guide/recommend-route` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"answer": null, "languageCode": "zh-CN", "costMillis": 2869}} |
| 34 | PASS | POST | `/api/ai/guide/common-question` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"answer": null, "languageCode": "zh-CN", "costMillis": 3655}} |
| 35 | PASS | POST | `/api/ai/guide/translate-answer` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"answer": null, "languageCode": "en-US", "costMillis": 2875}} |
| 36 | PASS | POST | `/api/file/upload` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 4, "fileName": "api-test.png", "fileUrl": "https://communityforum-backendd.o... |
| 37 | PASS | GET | `/api/admin/statistics/dashboard` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"totalUsers": 6, "totalScenicSpots": 10, "totalRoutes": 6, "totalFavorites": 6, "t... |
| 38 | PASS | GET | `/api/admin/user/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 10, "total": 6, "records": [{"id": 7, "username": "api_t... |
| 39 | PASS | POST | `/api/admin/user/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 40 | PASS | POST | `/api/admin/user/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 41 | PASS | GET | `/api/admin/scenic/category/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 6, "records": [{"id": 1, "categoryName": "自... |
| 42 | PASS | GET | `/api/admin/scenic/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 10, "records": [{"id": 1, "categoryId": 2, ... |
| 43 | PASS | GET | `/api/admin/scenic/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "categoryId": 2, "categoryName": "历史人文", "spotName": "故宫博物院", "city": "北京... |
| 44 | PASS | GET | `/api/admin/scenic/i18n/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "scenicSpotId": 1, "languageCode": "en-US", "title": "The Forbidden City... |
| 45 | PASS | GET | `/api/admin/scenic/media/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "scenicSpotId": 1, "mediaType": "image", "mediaUrl": "https://communityf... |
| 46 | PASS | GET | `/api/admin/route/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 6, "records": [{"id": 1, "routeName": "北京文化... |
| 47 | PASS | GET | `/api/admin/route/detail` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"id": 1, "routeName": "北京文化深度游", "theme": "历史文化", "coverUrl": null, "summary": "探索... |
| 48 | PASS | GET | `/api/admin/route/i18n/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 1, "routeId": 1, "languageCode": "en-US", "title": "Beijing Cultural Deep T... |
| 49 | PASS | GET | `/api/admin/feedback/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 5, "records": [{"id": 5, "userId": 2, "user... |
| 50 | PASS | GET | `/api/admin/ai/log/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 15, "records": [{"id": 15, "userId": 2, "us... |
| 51 | PASS | GET | `/api/admin/file/page` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": {"current": 1, "pageSize": 20, "total": 4, "records": [{"id": 4, "fileName": "api-t... |
| 52 | PASS | POST | `/api/admin/scenic/category/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 8} |
| 53 | PASS | POST | `/api/admin/scenic/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 12} |
| 54 | PASS | POST | `/api/admin/route/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 8} |
| 55 | PASS | POST | `/api/admin/scenic/category/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 8} |
| 56 | PASS | POST | `/api/admin/scenic/category/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 57 | PASS | POST | `/api/admin/scenic/category/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 58 | PASS | POST | `/api/admin/scenic/category/delete` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 59 | PASS | POST | `/api/admin/scenic/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 12} |
| 60 | PASS | POST | `/api/admin/scenic/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 61 | PASS | POST | `/api/admin/scenic/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 62 | PASS | POST | `/api/admin/scenic/i18n/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 7} |
| 63 | PASS | POST | `/api/admin/scenic/i18n/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 7} |
| 64 | PASS | POST | `/api/admin/scenic/media/add` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 7} |
| 65 | PASS | POST | `/api/admin/scenic/media/delete` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 66 | PASS | GET | `/api/admin/scenic/i18n/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 7, "scenicSpotId": 12, "languageCode": "fr-FR", "title": "Test Scenic Spot ... |
| 67 | PASS | GET | `/api/admin/scenic/media/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": []} |
| 68 | PASS | POST | `/api/admin/scenic/delete` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 69 | PASS | POST | `/api/admin/route/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 8} |
| 70 | PASS | POST | `/api/admin/route/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 71 | PASS | POST | `/api/admin/route/status` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 72 | PASS | POST | `/api/admin/route/i18n/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 4} |
| 73 | PASS | POST | `/api/admin/route/i18n/save` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": 4} |
| 74 | PASS | GET | `/api/admin/route/i18n/list` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": [{"id": 4, "routeId": 8, "languageCode": "fr-FR", "title": "Test Route Updated", "s... |
| 75 | PASS | POST | `/api/admin/route/delete` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 76 | PASS | POST | `/api/auth/logout` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 77 | PASS | GET | `/api/auth/me` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": null} |
| 78 | PASS | POST | `/api/auth/logout` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": true} |
| 79 | PASS | GET | `/api/auth/me` | 200 | 0 | ok | {"code": 0, "message": "ok", "data": null} |