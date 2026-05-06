package com.learning.englishpro.auth.dto.request;

import com.learning.englishpro.auth.entity.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Tên đăng nhập không được trống")
        @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
        String username,

        @NotBlank(message = "Email không được trống")
        @Email(message = "Định dạng email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu không được trống")
        @Size(min = 8, message = "Mật khẩu phải chứa ít nhất 8 ký tự")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "Mật khẩu phải chứa ít nhất một chữ hoa, một chữ số và một ký tự đặc biệt"
        )
        String password,
        @NotBlank(message = "Tên không được trống")
        String firstName,
        @NotBlank(message = "Họ không được trống")
        String lastName,
        @NotBlank(message = "Số điện thoại không được trống")
        @Size(min = 10, max = 10, message = "Số điện thoại phải gồm 10 chữ số")
        String phone,
        UserGender gender,
        @NotBlank(message = "Địa chỉ không được trống")
        String address
) {
        public String computeFullName() {
                return firstName + " " + lastName;
        }
}
