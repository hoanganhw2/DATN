/**
 * Mock Exam Data - TOEIC & IELTS
 * Author: Senior Backend Engineer
 * Purpose: Test data for exam features
 */

export type QuestionType =
  | 'SINGLE_CHOICE'
  | 'MULTIPLE_CHOICE'
  | 'TEXT_INPUT'
  | 'TRUE_FALSE_NOT_GIVEN'
export type ExamType = 'TOEIC' | 'IELTS' | 'MINI_TEST'

export interface Option {
  id: string
  text: string
  order: 1 | 2 | 3 | 4
}

export interface Question {
  id: string
  sectionId: string
  questionNumber: number
  questionText: string
  imageUrl?: string
  audioUrl?: string
  passage?: string // For Reading sections
  options: Option[]
  questionType: QuestionType
  correctAnswer: string // id của option hoặc text cho TEXT_INPUT
  point: number
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
}

export interface Section {
  id: string
  sectionName: string
  sectionOrder: number
  instructions: string
  questions: Question[]
}

export interface Exam {
  id: string
  title: string
  description: string
  type: ExamType
  duration: number // phút
  totalScore: number
  totalQuestions: number
  attempts: number // số lần đã làm
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  createdDate: string
  sections: Section[]
}

// ============ TOEIC DATA ============

const TOEIC_SECTION_1: Section = {
  id: 'toeic-part1',
  sectionName: 'Part 1: Photographs',
  sectionOrder: 1,
  instructions:
    'In this part, you will hear four statements about each picture. Listen and choose the statement that best describes what you see in the picture.',
  questions: [
    {
      id: 'toeic-1-1',
      sectionId: 'toeic-part1',
      questionNumber: 1,
      questionText: 'What is the most appropriate statement for this picture?',
      imageUrl: 'https://via.placeholder.com/400x300?text=Photo+1',
      audioUrl: 'https://example.com/audio/toeic-1-1.mp3',
      options: [
        { id: 'opt1', text: 'The man is writing on the whiteboard.', order: 1 },
        { id: 'opt2', text: 'The woman is sitting at a desk.', order: 2 },
        { id: 'opt3', text: 'The people are having a meeting.', order: 3 },
        { id: 'opt4', text: 'The room is empty.', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 5,
      difficulty: 'EASY',
    },
    {
      id: 'toeic-1-2',
      sectionId: 'toeic-part1',
      questionNumber: 2,
      questionText: 'Which statement best describes this image?',
      imageUrl: 'https://via.placeholder.com/400x300?text=Photo+2',
      audioUrl: 'https://example.com/audio/toeic-1-2.mp3',
      options: [
        { id: 'opt1', text: 'A keyboard is on the table.', order: 1 },
        { id: 'opt2', text: 'The laptop is turned off.', order: 2 },
        { id: 'opt3', text: 'There is coffee next to the computer.', order: 3 },
        { id: 'opt4', text: 'No one is using the computer.', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 5,
      difficulty: 'EASY',
    },
  ],
}

const TOEIC_SECTION_2: Section = {
  id: 'toeic-part2',
  sectionName: 'Part 2: Question-Response',
  sectionOrder: 2,
  instructions:
    'In this part, you will hear a question followed by three responses. Choose the response that best answers the question.',
  questions: [
    {
      id: 'toeic-2-1',
      sectionId: 'toeic-part2',
      questionNumber: 3,
      questionText: 'Audio question: When did you finish the project?',
      audioUrl: 'https://example.com/audio/toeic-2-1-q.mp3',
      options: [
        { id: 'opt1', text: 'Last Friday. (A)', order: 1 },
        { id: 'opt2', text: 'Very nice weather today. (B)', order: 2 },
        { id: 'opt3', text: 'I like working with you. (C)', order: 3 },
        { id: 'opt4', text: 'Not applicable', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 3,
      difficulty: 'EASY',
    },
    {
      id: 'toeic-2-2',
      sectionId: 'toeic-part2',
      questionNumber: 4,
      questionText: 'Audio question: How many employees work in your department?',
      audioUrl: 'https://example.com/audio/toeic-2-2-q.mp3',
      options: [
        { id: 'opt1', text: 'There are twelve of us. (A)', order: 1 },
        { id: 'opt2', text: 'We work in the morning. (B)', order: 2 },
        { id: 'opt3', text: 'Yes, I do. (C)', order: 3 },
        { id: 'opt4', text: 'Not applicable', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 3,
      difficulty: 'MEDIUM',
    },
  ],
}

const TOEIC_SECTION_3: Section = {
  id: 'toeic-part3',
  sectionName: 'Part 3: Short Conversations',
  sectionOrder: 3,
  instructions:
    'In this part, you will hear a short conversation. You will hear each conversation twice. After you hear a conversation, answer the questions about it.',
  questions: [
    {
      id: 'toeic-3-1',
      sectionId: 'toeic-part3',
      questionNumber: 5,
      questionText: 'What is the man going to do?',
      audioUrl: 'https://example.com/audio/toeic-3-1.mp3',
      options: [
        { id: 'opt1', text: 'Make a phone call', order: 1 },
        { id: 'opt2', text: 'Go to a meeting', order: 2 },
        { id: 'opt3', text: 'Send an email', order: 3 },
        { id: 'opt4', text: 'Attend a seminar', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt2',
      point: 3,
      difficulty: 'MEDIUM',
    },
  ],
}

const TOEIC_SECTION_4: Section = {
  id: 'toeic-part4',
  sectionName: 'Part 4: Short Talks',
  sectionOrder: 4,
  instructions:
    'In this part, you will hear a short talk. You will hear each talk twice. After you hear a talk, answer the questions about it.',
  questions: [
    {
      id: 'toeic-4-1',
      sectionId: 'toeic-part4',
      questionNumber: 6,
      questionText: 'What is the main topic of the talk?',
      audioUrl: 'https://example.com/audio/toeic-4-1.mp3',
      options: [
        { id: 'opt1', text: 'New product launch', order: 1 },
        { id: 'opt2', text: 'Company restructuring', order: 2 },
        { id: 'opt3', text: 'Training procedures', order: 3 },
        { id: 'opt4', text: 'Sales report review', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 3,
      difficulty: 'MEDIUM',
    },
  ],
}

const TOEIC_SECTION_5: Section = {
  id: 'toeic-part5',
  sectionName: 'Part 5: Incomplete Sentences',
  sectionOrder: 5,
  instructions:
    'In this part, you will read a sentence with a blank. Four words or phrases are given below the sentence. Choose the word or phrase that best completes the sentence.',
  questions: [
    {
      id: 'toeic-5-1',
      sectionId: 'toeic-part5',
      questionNumber: 7,
      questionText: 'The manager _____ the proposal to the board yesterday.',
      options: [
        { id: 'opt1', text: 'presented', order: 1 },
        { id: 'opt2', text: 'presents', order: 2 },
        { id: 'opt3', text: 'will present', order: 3 },
        { id: 'opt4', text: 'is presenting', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 1,
      difficulty: 'EASY',
    },
    {
      id: 'toeic-5-2',
      sectionId: 'toeic-part5',
      questionNumber: 8,
      questionText: 'We need to reduce our expenses by 20% _____ the end of the quarter.',
      options: [
        { id: 'opt1', text: 'by', order: 1 },
        { id: 'opt2', text: 'until', order: 2 },
        { id: 'opt3', text: 'during', order: 3 },
        { id: 'opt4', text: 'before', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt4',
      point: 1,
      difficulty: 'MEDIUM',
    },
  ],
}

const TOEIC_SECTION_6: Section = {
  id: 'toeic-part6',
  sectionName: 'Part 6: Text Completion',
  sectionOrder: 6,
  instructions:
    'In this part, you will read a passage with blanks. Choose the word or phrase that best fits in each blank.',
  questions: [
    {
      id: 'toeic-6-1',
      sectionId: 'toeic-part6',
      questionNumber: 9,
      questionText:
        'Passage excerpt: [___1___] the new policies will help improve employee satisfaction. We have taken feedback from various departments. [___2___], we now have a comprehensive plan to implement.',
      passage:
        'According to the new policies will help improve employee satisfaction. We have taken feedback from various departments. Therefore, we now have a comprehensive plan to implement.',
      options: [
        { id: 'opt1', text: 'According to', order: 1 },
        { id: 'opt2', text: 'In accordance with', order: 2 },
        { id: 'opt3', text: 'Based on', order: 3 },
        { id: 'opt4', text: 'Due to', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 2,
      difficulty: 'HARD',
    },
  ],
}

const TOEIC_SECTION_7: Section = {
  id: 'toeic-part7',
  sectionName: 'Part 7: Reading Comprehension',
  sectionOrder: 7,
  instructions:
    'In this part, you will read several passages. Each passage is followed by questions. Choose the best answer to each question based on what is stated or implied in the passage.',
  questions: [
    {
      id: 'toeic-7-1',
      sectionId: 'toeic-part7',
      questionNumber: 10,
      questionText: 'What is the purpose of this memo?',
      passage:
        'MEMORANDUM\n\nTO: All Staff\nFROM: Human Resources Department\nDATE: April 9, 2026\nRE: Summer Holiday Schedule\n\nPlease note that the summer holiday period will run from July 1 to August 31. Employees must submit their holiday requests by May 15. No more than 30% of staff can be on holiday at the same time.',
      options: [
        { id: 'opt1', text: 'To announce the summer holiday schedule', order: 1 },
        { id: 'opt2', text: 'To request holiday submissions', order: 2 },
        { id: 'opt3', text: 'To limit staff numbers', order: 3 },
        { id: 'opt4', text: 'To discuss holiday policies', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt1',
      point: 2,
      difficulty: 'EASY',
    },
  ],
}

export const MOCK_TOEIC_EXAM: Exam = {
  id: 'exam-toeic-001',
  title: 'TOEIC Simulation Test #1',
  description: 'TOEIC test with all 7 parts (10 sample questions)',
  type: 'TOEIC',
  duration: 120,
  totalScore: 990,
  totalQuestions: 10,
  attempts: 0,
  difficulty: 'MEDIUM',
  createdDate: '2026-04-01',
  sections: [
    TOEIC_SECTION_1,
    TOEIC_SECTION_2,
    TOEIC_SECTION_3,
    TOEIC_SECTION_4,
    TOEIC_SECTION_5,
    TOEIC_SECTION_6,
    TOEIC_SECTION_7,
  ],
}

// ============ IELTS DATA ============

const IELTS_READING_PASSAGE = `
THE HISTORY OF COFFEE

Coffee is one of the most consumed beverages worldwide, with millions of people starting their day with a cup of this aromatic drink. However, the history of coffee is far more complex and fascinating than many realize.

The origins of coffee can be traced back to Ethiopia, where coffee plants grew wild in the mountainous regions. According to legend, a goat herder named Kaldi discovered the stimulating effects of coffee around the 9th century when he noticed his goats becoming energized after eating berries from a coffee plant. This discovery eventually led to the cultivation of coffee in Ethiopia and its subsequent spread to the Arab world.

By the 15th century, coffee had become extremely popular in the Middle East, particularly in Mecca and Cairo. The drink was associated with religious ceremonies and later became the subject of much social and political debate. Coffee houses, known as "qahveh khaneh," became important social institutions where people gathered to discuss politics, philosophy, and literature. These establishments played a crucial role in the development of Arab culture and were often referred to as "schools of the wise."

The introduction of coffee to Europe happened relatively late, around the 17th century. Venetian merchants were among the first to bring coffee to Europe after trading with the Ottoman Empire. Initially, coffee was expensive and considered a luxury item available only to the wealthy. However, as production increased and prices decreased, coffee gradually became more accessible to the general population.

The Industrial Revolution had a profound impact on coffee consumption and production. New technologies, such as the espresso machine invented by Angelo Morondo in 1901, revolutionized how coffee was prepared and consumed. The instant coffee, developed by Nestlé in 1938, further increased coffee's accessibility and popularity worldwide.

Today, coffee is a global commodity with over 2 billion cups consumed daily. It remains an integral part of many cultures and continues to be a subject of scientific research regarding its health benefits and potential risks.
`

const IELTS_READING_SECTION: Section = {
  id: 'ielts-reading-1',
  sectionName: 'IELTS Reading: The History of Coffee',
  sectionOrder: 1,
  instructions:
    'Read the passage and answer the questions below. Choose TRUE, FALSE, or NOT GIVEN for each statement.',
  questions: [
    {
      id: 'ielts-r-1',
      sectionId: 'ielts-reading-1',
      questionNumber: 1,
      questionText:
        'According to the passage, the origins of coffee can be traced back to Ethiopia.',
      passage: IELTS_READING_PASSAGE,
      options: [
        { id: 'opt1', text: 'TRUE', order: 1 },
        { id: 'opt2', text: 'FALSE', order: 2 },
        { id: 'opt3', text: 'NOT GIVEN', order: 3 },
        { id: 'opt4', text: 'N/A', order: 4 },
      ],
      questionType: 'TRUE_FALSE_NOT_GIVEN',
      correctAnswer: 'opt1',
      point: 1,
      difficulty: 'EASY',
    },
    {
      id: 'ielts-r-2',
      sectionId: 'ielts-reading-1',
      questionNumber: 2,
      questionText:
        'Coffee houses in the Middle East were primarily used for religious ceremonies.',
      passage: IELTS_READING_PASSAGE,
      options: [
        { id: 'opt1', text: 'TRUE', order: 1 },
        { id: 'opt2', text: 'FALSE', order: 2 },
        { id: 'opt3', text: 'NOT GIVEN', order: 3 },
        { id: 'opt4', text: 'N/A', order: 4 },
      ],
      questionType: 'TRUE_FALSE_NOT_GIVEN',
      correctAnswer: 'opt2',
      point: 1,
      difficulty: 'MEDIUM',
    },
    {
      id: 'ielts-r-3',
      sectionId: 'ielts-reading-1',
      questionNumber: 3,
      questionText: 'Complete the sentence: Instant coffee was developed by ______ in 1938.',
      passage: IELTS_READING_PASSAGE,
      options: [
        { id: 'opt1', text: 'Nestlé', order: 1 },
        { id: 'opt2', text: 'Angelo Morondo', order: 2 },
        { id: 'opt3', text: 'Venetian merchants', order: 3 },
        { id: 'opt4', text: 'Ethiopian farmers', order: 4 },
      ],
      questionType: 'TEXT_INPUT',
      correctAnswer: 'Nestlé',
      point: 1,
      difficulty: 'EASY',
    },
    {
      id: 'ielts-r-4',
      sectionId: 'ielts-reading-1',
      questionNumber: 4,
      questionText:
        'According to the passage, how many cups of coffee are consumed daily worldwide?',
      passage: IELTS_READING_PASSAGE,
      options: [
        { id: 'opt1', text: '1 billion', order: 1 },
        { id: 'opt2', text: 'Over 2 billion', order: 2 },
        { id: 'opt3', text: '500 million', order: 3 },
        { id: 'opt4', text: '3 billion', order: 4 },
      ],
      questionType: 'SINGLE_CHOICE',
      correctAnswer: 'opt2',
      point: 1,
      difficulty: 'EASY',
    },
  ],
}

export const MOCK_IELTS_EXAM: Exam = {
  id: 'exam-ielts-001',
  title: 'IELTS Reading Practice Test',
  description: 'IELTS Reading module - 1 passage with 4 sample questions',
  type: 'IELTS',
  duration: 60,
  totalScore: 9,
  totalQuestions: 4,
  attempts: 0,
  difficulty: 'MEDIUM',
  createdDate: '2026-04-01',
  sections: [IELTS_READING_SECTION],
}

// ============ MINI TEST DATA ============

export const MOCK_MINI_TEST: Exam = {
  id: 'exam-mini-001',
  title: 'TOEIC Mini Test - Part 5 Only',
  description: 'Quick practice test - Part 5 Incomplete Sentences (2 sample questions)',
  type: 'MINI_TEST',
  duration: 10,
  totalScore: 100,
  totalQuestions: 2,
  attempts: 2,
  difficulty: 'EASY',
  createdDate: '2026-04-05',
  sections: [TOEIC_SECTION_5],
}

// ============ EXPORT ALL EXAMS ============

export const ALL_MOCK_EXAMS: Exam[] = [MOCK_TOEIC_EXAM, MOCK_IELTS_EXAM, MOCK_MINI_TEST]
