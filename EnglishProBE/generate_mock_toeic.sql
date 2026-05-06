-- Chèn dữ liệu giả cho đề thi TOEIC
-- Tắt kiểm tra khóa ngoại để tránh lỗi khi chèn dữ liệu không theo thứ tự (nếu cần)
SET FOREIGN_KEY_CHECKS = 0;

-- 0. Tìm một admin làm người tạo (Thay đổi role nếu cần)
SET @creator_id = (SELECT id FROM users WHERE role = 'ADMIN' OR role = 'ROLE_ADMIN' LIMIT 1);

-- 1. Chèn Exam
INSERT INTO exams (title, description, exam_type, level, duration, total_score, passing_score, max_attempts, is_published, creator_id, created_at, updated_at)
VALUES (
    'TOEIC Simulation Test #1 - Full 7 Parts', 
    'Đây là đề thi thử TOEIC rút gọn dùng để test tính năng. Bao gồm đầy đủ 7 phần với các loại câu hỏi khác nhau.', 
    'TOEIC', 
    'INTERMEDIATE', 
    7200, -- 120 phút
    990, 
    450.00, 
    3, 
    1, -- Đã xuất bản
    @creator_id, 
    NOW(), 
    NOW()
);

SET @exam_id = LAST_INSERT_ID();

-- 2. Chèn Exam Sections (7 Parts)
-- Part 1: Photographs
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 1: Photographs', 1, 300, 2, NOW(), NOW());
SET @section1_id = LAST_INSERT_ID();

-- Part 2: Question-Response
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 2: Question-Response', 2, 600, 2, NOW(), NOW());
SET @section2_id = LAST_INSERT_ID();

-- Part 3: Conversations
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 3: Conversations', 3, 900, 1, NOW(), NOW());
SET @section3_id = LAST_INSERT_ID();

-- Part 4: Short Talks
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 4: Short Talks', 4, 900, 1, NOW(), NOW());
SET @section4_id = LAST_INSERT_ID();

-- Part 5: Incomplete Sentences
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 5: Incomplete Sentences', 5, 600, 2, NOW(), NOW());
SET @section5_id = LAST_INSERT_ID();

-- Part 6: Text Completion
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 6: Text Completion', 6, 600, 1, NOW(), NOW());
SET @section6_id = LAST_INSERT_ID();

-- Part 7: Reading Comprehension
INSERT INTO exam_sections (exam_id, section_name, section_order, duration, total_questions, created_at, updated_at)
VALUES (@exam_id, 'Part 7: Reading Comprehension', 7, 1800, 1, NOW(), NOW());
SET @section7_id = LAST_INSERT_ID();

-- 3. Chèn Exam Questions
-- Part 1
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section1_id, 'Look at the picture. What is the most accurate description?', 'https://images.unsplash.com/photo-1497366216548-37526070297c', 'SINGLE_CHOICE', '["The people are having a meeting.", "The office is empty.", "The man is fixing the printer.", "They are eating in a restaurant."]', 'The people are having a meeting.', 'Hình ảnh hiển thị một nhóm người đang thảo luận trong văn phòng.', 5, 1, NOW(), NOW());

INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section1_id, 'What is the woman doing in the photo?', 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4', 'SINGLE_CHOICE', '["She is typing on a laptop.", "She is reading a newspaper.", "She is talking on the phone.", "She is drinking coffee."]', 'She is typing on a laptop.', 'Hình ảnh chụp một người phụ nữ đang làm việc với máy tính xách tay.', 5, 2, NOW(), NOW());

-- Part 2
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section2_id, '(Audio) When is the deadline for the marketing report?', NULL, 'SINGLE_CHOICE', '["By the end of this week.", "I like marketing.", "The report is 10 pages long.", "Yes, I did."]', 'By the end of this week.', 'Câu hỏi "When" yêu cầu một mốc thời gian.', 5, 1, NOW(), NOW());

INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section2_id, '(Audio) Would you like to go to lunch now or later?', NULL, 'SINGLE_CHOICE', '["Let''s go now.", "I had breakfast.", "The lunch was delicious.", "At the cafeteria."]', 'Let''s go now.', 'Câu hỏi lựa chọn "now or later".', 5, 2, NOW(), NOW());

-- Part 3
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section3_id, 'What problem does the woman mention?', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'SINGLE_CHOICE', '["The printer is broken.", "The meeting was canceled.", "She lost her keys.", "The flight is delayed."]', 'The printer is broken.', 'Người phụ nữ nói về việc máy in gặp sự cố kỹ thuật.', 5, 1, NOW(), NOW());

-- Part 4
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section4_id, 'Who is the speaker most likely addressing?', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'SINGLE_CHOICE', '["New employees.", "Potential clients.", "Store customers.", "Technical support team."]', 'New employees.', 'Bài nói hướng đến nhân viên mới trong buổi định hướng.', 5, 1, NOW(), NOW());

-- Part 5
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section5_id, 'The marketing team _____ the new campaign next Monday.', NULL, 'SINGLE_CHOICE', '["will launch", "launches", "launched", "is launch"]', 'will launch', 'Thì tương lai đơn (next Monday).', 5, 1, NOW(), NOW());

INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section5_id, 'Please review the document _____ before signing it.', NULL, 'SINGLE_CHOICE', '["carefully", "careful", "care", "caring"]', 'carefully', 'Trạng từ bổ nghĩa cho động từ "review".', 5, 2, NOW(), NOW());

-- Part 6
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section6_id, 'Complete the text: We are pleased to [___] our new partnership with ABC Corp.', NULL, 'SINGLE_CHOICE', '["announce", "announcing", "announcement", "announced"]', 'announce', 'Cấu trúc "be pleased to + V-inf".', 5, 1, NOW(), NOW());

-- Part 7
INSERT INTO exam_questions (section_id, question_text, media_url, question_type, options, correct_answer, explanation, points, order_index, created_at, updated_at)
VALUES (@section7_id, 'What is the purpose of the email?\n\nPassage: Dear Team, please be advised that the elevator will be under maintenance this Friday from 9 AM to 5 PM.', NULL, 'SINGLE_CHOICE', '["To inform about elevator maintenance", "To announce a holiday", "To request repair services", "To cancel a meeting"]', 'To inform about elevator maintenance', 'Nội dung email nói về việc bảo trì thang máy.', 5, 1, NOW(), NOW());

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;
