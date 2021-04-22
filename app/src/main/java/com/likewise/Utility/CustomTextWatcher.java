package com.likewise.Utility;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;



public class CustomTextWatcher {


        private TextWatcherWithInstance callback;

        public CustomTextWatcher setCallback(TextWatcherWithInstance callback) {
            this.callback = callback;
            return this;
        }

        public CustomTextWatcher registerEditText(final EditText editText) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    callback.beforeTextChanged(editText, s, start, count, after);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    callback.onTextChanged(editText, s, start, before, count);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    callback.afterTextChanged(editText, editable);
                }
            });

            return this;
        }

        public interface TextWatcherWithInstance {
            void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after);

            void onTextChanged(EditText editText, CharSequence s, int start, int before, int count);

            void afterTextChanged(EditText editText, Editable editable);
        }
    }

