package com.example.splitapp.dto;
public record ApiResponseDTO<T>(boolean success, String message, T data) {}