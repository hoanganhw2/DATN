# Demo Data Setup for Flashcard System

## Overview

Dữ liệu demo đã được cấu hình để test chức năng flashcard. Khi API backend không khả dụng hoặc kết nối thất bại, hệ thống sẽ tự động sử dụng dữ liệu mock thay vì throw error.

## Demo Data Files

### 1. **mockFlashcardData.ts** (`src/api/mockFlashcardData.ts`)

Chứa tất cả dữ liệu mock (giả định):

- **3 Bộ thẻ (Decks)** với thông tin đầy đủ
- **12-20 Thẻ (Cards)** cho mỗi bộ với status khác nhau
- **Helper functions** để lấy dữ liệu

### 2. **Deck 1: IELTS Vocabulary - Part 1**

- **ID:** 1
- **Tổng thẻ:** 12
- **Thẻ thành thạo:** 5
- **Thẻ cần ôn hôm nay:** 4
- **Cards có status:**
  - 2 Mastered (Abundant, Eloquent)
  - 2 Review (Accommodate, Facilitate)
  - 3 Learning (Ambiguous, Meticulous, Gregarious)
  - 5 New (Benevolent, Ephemeral, Heinous, Indefatigable, v.v.)

### 3. **Deck 2: Business English - Meetings**

- **ID:** 2
- **Tổng thẻ:** 18
- **Thẻ thành thạo:** 12
- **Thẻ cần ôn hôm nay:** 2
- **Cards có status:**
  - 6 Mastered (các từ cơ bản)
  - 1 Review
  - 1 Learning
  - 1 New

### 4. **Deck 3: Phrasal Verbs Level 1**

- **ID:** 3
- **Tổng thẻ:** 20
- **Thẻ thành thạo:** 8
- **Thẻ cần ôn hôm nay:** 6
- **Cards có status:** Mixed (New, Learning, Review, Mastered)

## Features in Demo Data

Mỗi thẻ trong demo data chứa:

```typescript
{
  id: number
  deckId: number
  front: string              // Từ tiếng Anh
  back: string               // Nghĩa tiếng Việt
  example_sentence?: string  // Câu ví dụ
  status: 'new' | 'learning' | 'review' | 'mastered'
  nextReviewDate: string     // Ngày ôn tập tiếp theo (ISO String)
  interval: number           // Khoảng cách ngày ôn tập
  easeFactor: number         // Hệ số khó (SM-2 algorithm)
  repetitions: number        // Số lần ôn tập
}
```

## Cách Sử Dụng Demo Data

### 1. **Tự động Fallback**

Khi API fail, API functions sẽ tự động return mock data:

```typescript
// Trong API flashcard.ts
try {
  const response = await axios.get('/v1/flashcard-decks')
  return response.data
} catch (error) {
  // Tự động fallback to mock data
  return mockDecks
}
```

### 2. **Test Flow:**

**Khởi động ứng dụng:**

```bash
npm run dev
```

**Các bước test:**

1. **Xem danh sách bộ thẻ:**
   - Chuyển đến `/flashcard`
   - Nên thấy 3 bộ thẻ demo có hình ảnh, số thẻ, và dữ liệu thống kê

2. **Xem chi tiết bộ thẻ:**
   - Click vào một bộ thẻ
   - Nên thấy danh sách tất cả 12-20 thẻ với status (New/Learning/Review/Mastered)
   - Xem thống kê: Tổng thẻ, Mới, Đang học, Thành thạo

3. **Review flashcard:**
   - Click "Ôn tập" hoặc "Học"
   - Nên thấy thẻ lật (flip card) với animation
   - Mặt trước: Từ tiếng Anh (sau click/Enter show)
   - Mặt sau: Nghĩa tiếng Việt + ví dụ câu
   - Đánh giá: 1=Again, 2=Hard, 3=Good, 4=Easy
   - Keyboard shortcuts: Space để lật, 1-4 để đánh giá

4. **Tạo bộ thẻ mới:**
   - Chuyển đến `/flashcard/create`
   - Nhập tên bộ thẻ
   - Thêm thẻ (front/back)
   - Click "Thêm hàng" để thêm nhiều thẻ
   - Click AI suggestion để lấy ví dụ (fallback to mock examples)
   - Submit: Sẽ tạo new deck với ID random

## Mock API Responses

Tất cả API functions có fallback responses:

| API Function                    | Mock Response                             |
| ------------------------------- | ----------------------------------------- |
| `getFlashcardDecks()`           | 3 decks từ `mockDecks`                    |
| `getDeckCards(deckId)`          | Cards cho deck cụ thể                     |
| `getCardsToReviewToday(deckId)` | Cards có `nextReviewDate <= today`        |
| `submitCardReview()`            | Random nextReviewDate (demo)              |
| `createFlashcardDeck()`         | New deck object với ID = timestamp        |
| `addCardsToDeck()`              | Array của cards với status='new'          |
| `getFlashcardStats()`           | Counted from mock cards                   |
| `getUserStreak()`               | `{ currentStreak: 7, longestStreak: 15 }` |
| `getAISuggestions(word)`        | Demo examples từ mock data                |

## Transition to Real API

Khi backend ready, chỉ cần:

1. Đảm bảo API endpoints khớp
2. Xóa hoặc comment các mock data fallbacks
3. Hoặc giữ fallback để development/testing khi API down

## Testing Checklist

- [ ] FlashcardListView hiển thị 3 demo decks
- [ ] Thống kê đúng: Total/New/Learning/Mastered
- [ ] DeckDetailView hiển thị cards của deck
- [ ] FlashcardReviewView lật thẻ với animation
- [ ] SM-2 buttons (1,2,3,4) hoạt động
- [ ] Keyboard shortcuts (Space, 1-4) hoạt động
- [ ] CreateFlashcardView form + submit
- [ ] FlashcardStats component hiển thị đúng%
- [ ] Navigation giữa các routes
- [ ] Responsive design trên mobile/tablet

## Notes

- Demo data được thiết kế để test UI/UX workflows
- `nextReviewDate` được tính toán động (relative to today)
- Cards status phân bố realistic (mostly new, some mastered)
- Mỗi card có `example_sentence` để test rendering
- Mock delays/animations preserved để tương tự real API

---

**Created:** April 9, 2026  
**Last Updated:** April 9, 2026
