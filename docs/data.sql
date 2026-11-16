USE outdoor_db;
SET NAMES utf8mb4;

-- 演示账号（密码均为 123456）
-- admin / 123456       管理员
-- guide_lin / 123456   领队
-- guide_chen / 123456  领队
-- guide_zhou / 123456  领队
-- user_xia / 123456    普通用户
-- user_ming / 123456   普通用户
INSERT INTO sys_user (username, password, phone, email, avatar, role, status) VALUES
('admin', MD5('123456'), '13800000000', 'admin@outdoor.com', NULL, 2, 1),
('guide_lin', MD5('123456'), '13800000001', 'lin@outdoor.com', NULL, 1, 1),
('guide_chen', MD5('123456'), '13800000002', 'chen@outdoor.com', NULL, 1, 1),
('guide_zhou', MD5('123456'), '13800000003', 'zhou@outdoor.com', NULL, 1, 1),
('user_xia', MD5('123456'), '13800000004', 'xia@outdoor.com', NULL, 0, 1),
('user_ming', MD5('123456'), '13800000005', 'ming@outdoor.com', NULL, 0, 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), status = 1;

INSERT INTO guide (user_id, real_name, id_card, intro, tags, price, city, status)
SELECT id, '林川', '330101199001011234',
       '国家职业资格户外领队，8 年高山徒步经验，擅长江浙沪周边轻装徒步与露营。',
       '徒步,露营,登山', 268.00, '杭州', 1
FROM sys_user WHERE username = 'guide_lin'
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), intro = VALUES(intro), tags = VALUES(tags),
                        price = VALUES(price), city = VALUES(city), status = 1;

INSERT INTO guide (user_id, real_name, id_card, intro, tags, price, city, status)
SELECT id, '陈野', '310101199205052345',
       '山地车与公路骑行领队，熟悉环太湖、千岛湖线路，注重安全与节奏控制。',
       '骑行,公路车,摄影', 198.00, '上海', 1
FROM sys_user WHERE username = 'guide_chen'
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), intro = VALUES(intro), tags = VALUES(tags),
                        price = VALUES(price), city = VALUES(city), status = 1;

INSERT INTO guide (user_id, real_name, id_card, intro, tags, price, city, status)
SELECT id, '周岚', '440101199408083456',
       '攀岩教练与溯溪向导，拥有红十字急救证，带你安全探索山野。',
       '攀岩,溯溪,亲子', 328.00, '广州', 1
FROM sys_user WHERE username = 'guide_zhou'
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), intro = VALUES(intro), tags = VALUES(tags),
                        price = VALUES(price), city = VALUES(city), status = 1;

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '莫干山竹海轻徒步', '徒步', 1, '浙江湖州·莫干山', '莫干山镇游客中心',
       DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY) + INTERVAL 8 HOUR,
       268.00, 12, 3,
       'https://images.unsplash.com/photo-1551632811-561732d1e306?auto=format&fit=crop&w=1200&q=80',
       '路线亮点：竹海秘境、剑池飞瀑、山顶观景台。全程约 9 公里，累计爬升 420 米，适合新手。费用包含领队服务、户外保险与急救包。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_lin' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '莫干山竹海轻徒步');

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '千岛湖环湖骑行一日', '骑行', 2, '浙江杭州·千岛湖', '千岛湖广场',
       DATE_ADD(NOW(), INTERVAL 8 DAY), DATE_ADD(NOW(), INTERVAL 8 DAY) + INTERVAL 7 HOUR,
       198.00, 16, 6,
       'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&w=1200&q=80',
       '沿湖绿道骑行约 45 公里，配专业公路车与头盔。途中设置 3 个补给点，领队全程压队，适合有基础骑行能力的伙伴。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_chen' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '千岛湖环湖骑行一日');

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '英西峰林攀岩体验', '攀岩', 3, '广东清远·英西峰林', '黄花镇集合点',
       DATE_ADD(NOW(), INTERVAL 12 DAY), DATE_ADD(NOW(), INTERVAL 12 DAY) + INTERVAL 6 HOUR,
       328.00, 10, 4,
       'https://images.unsplash.com/photo-1522163182402-834f871fd851?auto=format&fit=crop&w=1200&q=80',
       '零基础也可参加。包含攀岩装备、教练指导与保护站设置，体验喀斯特峰林中的垂直世界。建议穿运动服和硬底鞋。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_zhou' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '英西峰林攀岩体验');

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '临安大明山观星露营', '露营', 1, '浙江杭州·大明山', '大明山景区南门',
       DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 16 DAY),
       398.00, 20, 9,
       'https://images.unsplash.com/photo-1478131143081-80f7f84ca84d?auto=format&fit=crop&w=1200&q=80',
       '傍晚徒步上山，扎营后煮咖啡、看日落，夜里识别星座。提供帐篷、防潮垫与晚餐，自带睡袋即可。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_lin' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '临安大明山观星露营');

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '苏州太湖亲子骑行', '骑行', 1, '江苏苏州·太湖', '太湖游客中心',
       DATE_ADD(NOW(), INTERVAL 18 DAY), DATE_ADD(NOW(), INTERVAL 18 DAY) + INTERVAL 4 HOUR,
       158.00, 18, 2,
       'https://images.unsplash.com/photo-1502744688674-c619d1586c9e?auto=format&fit=crop&w=1200&q=80',
       '适合 8 岁以上儿童的亲子骑行，全程 18 公里，节奏轻松。包含儿童车、头盔、保险与下午茶。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_chen' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '苏州太湖亲子骑行');

INSERT INTO activity (guide_id, title, type, difficulty, destination, gather_point,
                      start_time, end_time, price, max_num, current_num, cover, content, status)
SELECT g.id, '从化溪谷溯溪探险', '溯溪', 2, '广东广州·从化', '从化客运站',
       DATE_ADD(NOW(), INTERVAL 22 DAY), DATE_ADD(NOW(), INTERVAL 22 DAY) + INTERVAL 6 HOUR,
       288.00, 14, 5,
       'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&q=80',
       '逆流而上穿越溪谷，体验天然滑水与跳潭。提供溯溪鞋、头盔、救生衣，全程教练保护。',
       1
FROM guide g JOIN sys_user u ON g.user_id = u.id
WHERE u.username = 'guide_zhou' AND NOT EXISTS (SELECT 1 FROM activity WHERE title = '从化溪谷溯溪探险');

INSERT INTO booking (activity_id, user_id, guide_id, book_date, num, total_price, status, pay_time, create_time)
SELECT a.id, u.id, a.guide_id, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 2, a.price * 2, 3,
       DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM activity a JOIN sys_user u ON u.username = 'user_xia'
WHERE a.title = '莫干山竹海轻徒步'
  AND NOT EXISTS (SELECT 1 FROM booking b WHERE b.activity_id = a.id AND b.user_id = u.id);

INSERT INTO review (booking_id, activity_id, user_id, guide_id, rating, content)
SELECT b.id, b.activity_id, b.user_id, b.guide_id, 5,
       '林川领队非常专业，路线安排合理，沿途讲解也很有趣，第一次徒步体验满分！'
FROM booking b JOIN sys_user u ON b.user_id = u.id
WHERE u.username = 'user_xia'
  AND b.activity_id = (SELECT id FROM activity WHERE title = '莫干山竹海轻徒步')
  AND NOT EXISTS (SELECT 1 FROM review r WHERE r.booking_id = b.id);

INSERT INTO message (from_user_id, to_user_id, content, is_read)
SELECT NULL, u.id, '欢迎加入户外运动伴游预约平台，去发现下一次山野出发吧。', 0
FROM sys_user u
WHERE u.username IN ('user_xia', 'user_ming', 'guide_lin', 'guide_chen', 'guide_zhou')
  AND NOT EXISTS (SELECT 1 FROM message m WHERE m.to_user_id = u.id AND m.from_user_id IS NULL);
