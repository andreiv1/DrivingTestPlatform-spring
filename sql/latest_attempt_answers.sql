SELECT exam_attempt_id, order_index + 1 AS question_nr, answer_text, is_correct
FROM exam_attempt_questions exq
         JOIN answers a
              ON exq.question_id = a.question_id
WHERE exam_attempt_id = (
    SELECT MAX(exam_attempt_id)
    FROM exam_attempt_questions
) AND is_correct = True
ORDER BY order_index;
