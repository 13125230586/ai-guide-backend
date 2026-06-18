-- 初始化数据
USE `ai_guide_platform`;

-- 1. 用户数据 (密码都是 123456)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `avatar_url`, `role_code`, `user_status`, `email`, `phone`, `last_login_time`) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', NULL, 'ADMIN', 1, 'admin@aiguide.com', '13800000001', NOW()),
(2, 'tourist', 'e10adc3949ba59abbe56e057f20f883e', '游客用户', NULL, 'TOURIST', 1, 'tourist@test.com', '13800000002', NOW()),
(3, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', NULL, 'TOURIST', 1, 'zhangsan@test.com', '13800000003', NULL),
(4, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', NULL, 'TOURIST', 1, 'lisi@test.com', '13800000004', NULL),
(5, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', NULL, 'TOURIST', 0, 'wangwu@test.com', '13800000005', NULL);

-- 2. 景点分类
INSERT INTO `scenic_category` (`id`, `category_name`, `category_desc`, `sort_no`, `category_status`) VALUES
(1, '自然风光', '山水、湖泊、森林等自然景观', 1, 1),
(2, '历史人文', '古迹、博物馆、文化遗址等', 2, 1),
(3, '现代都市', '城市地标、商业区、现代建筑', 3, 1),
(4, '主题乐园', '游乐园、动物园、水族馆等', 4, 1),
(5, '古镇古村', '传统村落、古镇、民俗村', 5, 1),
(6, '海滨度假', '海滩、海岛、海滨浴场', 6, 1);

-- 3. 景点数据
INSERT INTO `scenic_spot` (`id`, `category_id`, `spot_name`, `city`, `address`, `longitude`, `latitude`, `cover_url`, `summary`, `description`, `open_time`, `suggest_duration`, `tips`, `hot_score`, `spot_status`, `creator_id`, `view_count`) VALUES
(1, 2, '故宫博物院', '北京', '北京市东城区景山前街4号', 116.397228, 39.916527, NULL, '中国明清两代的皇家宫殿，世界上现存规模最大的宫殿型建筑', '故宫博物院成立于1925年，是以明清两代皇宫和宫廷旧藏文物为基础建立的大型综合性博物馆。占地72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。收藏有超过186万件文物，是世界上现存规模最大、保存最为完整的木质结构古建筑之一。', '08:30-17:00（周一闭馆）', '3-4小时', '建议提前网上预约购票，周一闭馆', 999, 1, 1, 15680),
(2, 2, '秦始皇兵马俑', '西安', '陕西省西安市临潼区秦陵北路', 109.278751, 34.373682, NULL, '世界第八大奇迹，中国第一位皇帝的地下军阵', '兵马俑博物馆是中国最大的古代军事博物馆，位于秦始皇陵东侧1.5公里处。已发掘三座兵马俑坑，面积2万多平方米，出土陶俑、陶马约8000件。每个陶俑的面容、发型、手势各不相同，堪称世界雕塑艺术的瑰宝。', '08:30-18:00', '3-4小时', '旺季建议提前3天预约', 980, 1, 1, 12350),
(3, 1, '九寨沟', '阿坝', '四川省阿坝藏族羌族自治州九寨沟县', 103.917165, 33.263078, NULL, '人间仙境，童话世界，世界自然遗产', '九寨沟以翠海、叠瀑、彩林、雪峰、藏情、蓝冰六绝著称于世。景区内有114个海子，17个瀑布群，11个溪流，5个钙化滩流。被誉为"美丽的童话世界"，1992年被列入世界自然遗产名录。', '全天开放', '1-2天', '海拔较高，注意高原反应；秋季最美', 970, 1, 1, 9870),
(4, 1, '黄山', '黄山', '安徽省黄山市黄山区汤口镇', 118.168833, 30.137418, NULL, '五岳归来不看山，黄山归来不看岳', '黄山集名山之长，有泰山之雄伟、华山之险峻、衡山之烟云、庐山之瀑布、雁荡之巧石、峨眉之秀丽。以奇松、怪石、云海、温泉"四绝"闻名于世。徐霞客曾赞叹："薄海内外无如徽之黄山，登黄山天下无山，观止矣！"', '06:00-17:30', '2天', '建议住山上看日出日出；冬季注意防滑', 960, 1, 1, 8540),
(5, 2, '西湖', '杭州', '浙江省杭州市西湖区龙井路1号', 120.148732, 30.242828, NULL, '欲把西湖比西子，淡妆浓抹总相宜', '西湖位于杭州市区西面，南北长3.3公里，东西宽2.8公里，水面面积约5.66平方公里。环湖四周，绿荫环抱，山色葱茏，画桥烟柳，云树笼纱。西湖十景形成于南宋时期，苏堤春晓、曲院风荷、平湖秋月、断桥残雪等闻名遐迩。2011年列入世界文化遗产。', '全天开放', '1天', '免费开放，建议骑行环湖；夜游也很美', 950, 1, 1, 11200),
(6, 5, '丽江古城', '丽江', '云南省丽江市古城区', 100.227051, 26.872108, NULL, '东方威尼斯，世界文化遗产', '丽江古城始建于宋末元初，已有800多年历史。古城依山傍水，以水为主，家家流水，户户垂杨。城内街道用五花石铺砌，既有山城风貌，又有水乡韵味。纳西古乐、东巴文化、白沙壁画等人文景观令人流连忘返。', '全天开放', '2-3天', '古城维护费50元；注意防晒', 940, 1, 1, 7650),
(7, 6, '三亚亚龙湾', '三亚', '海南省三亚市吉阳区亚龙湾', 109.639282, 18.191099, NULL, '天下第一湾，东方夏威夷', '亚龙湾位于三亚市东南28公里处，是海南最南端的一个半月形海湾，全长约7.5公里。这里有蓝天、碧海、白沙、绿林，被誉为"天下第一湾"。海水清澈见底，能见度达7-9米，是理想的潜水胜地。', '全天开放', '2-3天', '冬季最佳；注意防晒和水上安全', 930, 1, 1, 6890),
(8, 4, '上海迪士尼乐园', '上海', '上海市浦东新区川沙镇', 121.667361, 31.143321, NULL, '点亮心中奇梦', '上海迪士尼乐园是中国内地首座迪士尼主题乐园，于2016年6月16日开幕。拥有六大主题园区：米奇大街、奇想花园、探险岛、宝藏湾、梦幻世界和明日世界。拥有全球迪士尼主题乐园中最大城堡——奇幻童话城堡。', '08:30-20:30', '1天', '建议工作日前往；下载APP查看排队时间', 920, 1, 1, 13500),
(9, 3, '上海外滩', '上海', '上海市黄浦区中山东一路', 121.493676, 31.230459, NULL, '万国建筑博览群，上海标志性景观', '外滩位于上海市中心黄浦区的黄浦江畔，是最具上海城市象征意义的景观之一。全长1.5公里，东侧 facing 黄浦江，西侧为52幢风格各异的大楼，被称为"万国建筑博览群"。夜晚的外滩灯火辉煌，与对岸的陆家嘴摩天大楼群交相辉映。', '全天开放', '1-2小时', '夜景最美；建议傍晚前往', 910, 1, 1, 10200),
(10, 1, '张家界国家森林公园', '张家界', '湖南省张家界市武陵源区', 110.479796, 29.326168, NULL, '缩小的仙境，放大的盆景', '张家界国家森林公园是中国第一个国家森林公园，以峰称奇、以谷显幽、以林见秀。三千座石峰拔地而起，八百条溪流蜿蜒曲折。电影《阿凡达》中的悬浮山就是以这里的南天一柱（哈利路亚山）为原型。', '07:00-18:00', '2-3天', '山路较多，穿舒适运动鞋；雨季注意防滑', 900, 1, 1, 7230);

-- 4. 景点多语种内容
INSERT INTO `scenic_spot_i18n` (`id`, `scenic_spot_id`, `language_code`, `title`, `summary`, `description`, `tips`) VALUES
(1, 1, 'en-US', 'The Forbidden City', 'The imperial palace of Chinese emperors', 'The Forbidden City, located in the center of Beijing, was the Chinese imperial palace from the Ming dynasty to the end of the Qing dynasty. It is now home to the Palace Museum.', 'Advance booking recommended. Closed on Mondays.'),
(2, 1, 'ja-JP', '故宮博物院', '中国明清時代の皇宮', '故宮博物院は、明・清時代の皇帝の宮殿を基に設立された博物館です。72万平方メートルの敷地に9000以上の部屋があります。', '事前予約が必要です。月曜日は休館です。'),
(3, 2, 'en-US', 'Terracotta Warriors', 'The Eighth Wonder of the World', 'The Terracotta Army is a collection of terracotta sculptures depicting the armies of Qin Shi Huang, the first Emperor of China.', 'Book tickets 3 days in advance during peak season.'),
(4, 5, 'en-US', 'West Lake', 'A UNESCO World Heritage Site', 'West Lake is a freshwater lake in Hangzhou, China. It has influenced poets and painters throughout Chinese history for its natural beauty and historic relics.', 'Free admission. Cycling around the lake is recommended.'),
(5, 3, 'en-US', 'Jiuzhaigou Valley', 'A Fairyland on Earth', 'Jiuzhaigou is a nature reserve and national park in Sichuan province, famous for its multi-colored lakes, waterfalls, and snow-capped peaks.', 'High altitude - watch for altitude sickness. Autumn is the most beautiful season.');

-- 5. 景点媒体资源
INSERT INTO `scenic_spot_media` (`id`, `scenic_spot_id`, `media_type`, `media_url`, `media_name`, `sort_no`) VALUES
(1, 1, 'image', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/forbidden-city-1.jpg', '故宫全景', 1),
(2, 1, 'image', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/forbidden-city-2.jpg', '太和殿', 2),
(3, 2, 'image', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/terracotta-1.jpg', '兵马俑一号坑', 1),
(4, 3, 'image', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/jiuzhaigou-1.jpg', '九寨沟五花海', 1),
(5, 5, 'image', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/westlake-1.jpg', '西湖全景', 1);

-- 6. 路线数据
INSERT INTO `guide_route` (`id`, `route_name`, `theme`, `cover_url`, `summary`, `description`, `suggest_duration`, `suitable_crowd`, `route_status`, `creator_id`) VALUES
(1, '北京文化深度游', '历史文化', NULL, '探索千年古都的文化底蕴', '这条路线将带你深入北京的历史文化核心，从皇家宫殿到现代艺术，感受这座城市的历史与现代交融之美。', '2天', '文化爱好者/家庭', 1, 1),
(2, '西安古都探秘', '历史文化', NULL, '穿越千年，梦回大唐', '西安，十三朝古都，丝绸之路的起点。这条路线将带你领略兵马俑的壮观、古城墙的雄伟、回民街的美食。', '2天', '历史爱好者/亲子', 1, 1),
(3, '杭州诗意之旅', '自然风光', NULL, '上有天堂，下有苏杭', '漫步西湖边，品味龙井茶，感受杭州的诗意与浪漫。这条路线适合慢慢走、慢慢看。', '1-2天', '情侣/文艺青年', 1, 1),
(4, '云南秘境之旅', '自然风光', NULL, '从雪山到热带雨林', '丽江古城的悠闲、玉龙雪山的壮美、泸沽湖的神秘，云南给你一段难忘的旅程。', '5-7天', '摄影爱好者/情侣', 1, 1),
(5, '上海摩登都市', '现代都市', NULL, '东方明珠的璀璨夜色', '外滩的万国建筑、陆家嘴的摩天大楼、迪士尼的童话世界，感受上海的摩登魅力。', '2-3天', '都市探索者/亲子', 1, 1),
(6, '海南阳光海滩', '海滨度假', NULL, '椰风海韵，阳光沙滩', '亚龙湾的碧海蓝天、蜈支洲岛的潜水天堂、南山寺的佛教文化，享受海南的热带风情。', '4-5天', '度假休闲/家庭', 1, 1);

-- 7. 路线景点关联
INSERT INTO `guide_route_spot_rel` (`id`, `route_id`, `scenic_spot_id`, `sort_no`, `stay_duration`) VALUES
(1, 1, 1, 1, '4小时'),
(2, 1, 9, 2, '2小时'),
(3, 2, 2, 1, '4小时'),
(4, 3, 5, 1, '1天'),
(5, 4, 6, 1, '2天'),
(6, 5, 8, 1, '1天'),
(7, 5, 9, 2, '2小时'),
(8, 6, 7, 1, '3天');

-- 8. 路线多语种内容
INSERT INTO `guide_route_i18n` (`id`, `route_id`, `language_code`, `title`, `summary`, `description`, `travel_tips`) VALUES
(1, 1, 'en-US', 'Beijing Cultural Deep Tour', 'Explore the cultural heritage of the ancient capital', 'This route takes you deep into the cultural heart of Beijing.', 'Wear comfortable shoes. Book Forbidden City tickets in advance.'),
(2, 3, 'en-US', 'Hangzhou Poetic Journey', 'Paradise on Earth', 'Stroll along West Lake, taste Longjing tea, and experience the poetry of Hangzhou.', 'Rent a bicycle to explore the lake area.');

-- 9. 收藏数据
INSERT INTO `user_favorite` (`id`, `user_id`, `biz_type`, `biz_id`) VALUES
(1, 2, 'SCENIC', 1),
(2, 2, 'SCENIC', 3),
(3, 2, 'ROUTE', 1),
(4, 3, 'SCENIC', 5),
(5, 3, 'SCENIC', 6),
(6, 4, 'SCENIC', 1),
(7, 4, 'ROUTE', 3);

-- 10. 反馈数据
INSERT INTO `user_feedback` (`id`, `user_id`, `feedback_type`, `content`, `contact_info`, `feedback_status`, `reply_content`, `reply_time`) VALUES
(1, 2, 'SUGGESTION', '希望能增加更多英文讲解内容', 'tourist@test.com', 1, '感谢建议，我们正在持续完善多语种内容', NOW()),
(2, 3, 'BUG', '景点详情页图片加载较慢', NULL, 0, NULL, NULL),
(3, 4, 'SUGGESTION', '希望增加语音导览功能', 'zhangsan@test.com', 0, NULL, NULL);

-- 11. AI调用日志
INSERT INTO `ai_guide_log` (`id`, `user_id`, `model_name`, `biz_type`, `language_code`, `scenic_spot_id`, `request_summary`, `response_summary`, `success_flag`, `cost_millis`) VALUES
(1, 2, 'MiniMax-M2.7', 'EXPLAIN', 'zh-CN', 1, '请讲解故宫的历史', '故宫博物院成立于1925年...', 1, 2350),
(2, 2, 'MiniMax-M2.7', 'QUESTION', 'en-US', 1, 'What is the history of the Forbidden City?', 'The Forbidden City was built...', 1, 1890),
(3, 3, 'MiniMax-M2.7', 'RECOMMEND', 'zh-CN', NULL, '推荐杭州一日游路线', '建议您从西湖开始...', 1, 3200),
(4, 4, 'MiniMax-M2.7', 'COMPARE', 'zh-CN', NULL, '对比故宫和兵马俑', '故宫和兵马俑都是...', 1, 2780);

-- 12. 文件资源
INSERT INTO `file_resource` (`id`, `biz_type`, `biz_id`, `file_name`, `file_url`, `file_type`, `file_size`, `storage_mode`, `uploader_id`) VALUES
(1, 'scenic', 1, 'forbidden-city.jpg', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/scenic/forbidden-city-1.jpg', 'jpg', 256000, 'oss', 1),
(2, 'user_avatar', 2, 'avatar.jpg', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/user_avatar/tourist.jpg', 'jpg', 32000, 'oss', 2);

