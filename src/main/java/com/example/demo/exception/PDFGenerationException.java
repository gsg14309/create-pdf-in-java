package com.example.demo.exception;

public class PDFGenerationException  extends RuntimeException{

    public PDFGenerationException(String message) {
        super(message);
    }

    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDFGenerationException(Throwable cause) {
        super(cause);
    }
}
