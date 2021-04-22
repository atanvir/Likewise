package com.likewise.Utility;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

public class MyPasswordTransformation extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source; 
        }
        public char charAt(int index) {
            Character ch=mSource.charAt(index);
            if(ch.equals(' ')) return ' ';
            else return '●';
        }
        public int length() {
            return mSource.length(); 
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}
