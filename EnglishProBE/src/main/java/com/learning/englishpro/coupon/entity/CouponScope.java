package com.learning.englishpro.coupon.entity;

public enum CouponScope {
    /** Admin tạo — áp dụng toàn bộ khóa học, phần giảm do nền tảng chịu, doanh thu teacher vẫn tính giá gốc */
    GLOBAL,
    /** Teacher tạo — chỉ áp dụng cho khóa của mình, phần giảm do teacher chịu */
    SPECIFIC
}
