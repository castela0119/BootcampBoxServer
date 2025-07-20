-- 테스트용 알림 데이터 생성
-- 사용자 ID가 1, 2, 3이 있다고 가정

-- 댓글 알림 테스트
INSERT INTO notifications (user_id, sender_id, type, title, content, target_type, target_id, is_read, created_at) VALUES
(1, 2, 'comment', '새 댓글', '김철수님이 회원님의 게시글에 댓글을 남겼습니다.', 'post', 1, false, NOW() - INTERVAL 1 HOUR),
(1, 3, 'comment', '새 댓글', '이영희님이 회원님의 게시글에 댓글을 남겼습니다.', 'post', 2, false, NOW() - INTERVAL 2 HOUR);

-- 좋아요 알림 테스트
INSERT INTO notifications (user_id, sender_id, type, title, content, target_type, target_id, is_read, created_at) VALUES
(1, 2, 'like', '좋아요', '김철수님이 회원님의 게시글에 좋아요를 눌렀습니다.', 'post', 1, false, NOW() - INTERVAL 30 MINUTE),
(1, 3, 'like', '좋아요', '이영희님이 회원님의 댓글에 좋아요를 눌렀습니다.', 'comment', 1, true, NOW() - INTERVAL 1 DAY);

-- 시스템 알림 테스트
INSERT INTO notifications (user_id, sender_id, type, title, content, target_type, target_id, is_read, created_at) VALUES
(1, NULL, 'system', '공지사항', '새로운 기능이 추가되었습니다. 확인해보세요!', 'system', NULL, false, NOW() - INTERVAL 3 HOUR),
(2, NULL, 'system', '시스템 업데이트', '앱이 업데이트되었습니다. 새로운 기능을 확인해보세요.', 'system', NULL, false, NOW() - INTERVAL 6 HOUR);

-- 다른 사용자들의 알림
INSERT INTO notifications (user_id, sender_id, type, title, content, target_type, target_id, is_read, created_at) VALUES
(2, 1, 'comment', '새 댓글', '홍길동님이 회원님의 게시글에 댓글을 남겼습니다.', 'post', 3, false, NOW() - INTERVAL 45 MINUTE),
(3, 1, 'like', '좋아요', '홍길동님이 회원님의 게시글에 좋아요를 눌렀습니다.', 'post', 4, false, NOW() - INTERVAL 15 MINUTE);

-- 확인
SELECT 
    n.id,
    u.username as recipient,
    s.username as sender,
    n.type,
    n.title,
    n.content,
    n.is_read,
    n.created_at
FROM notifications n
LEFT JOIN users u ON n.user_id = u.id
LEFT JOIN users s ON n.sender_id = s.id
ORDER BY n.created_at DESC; 