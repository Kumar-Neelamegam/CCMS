package vcc.cretivemindsz.kumar.utilities;

import android.support.v7.widget.AppCompatEditText;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.regex.Pattern;

public class Validation {

	// Regular Expression
	// you can change the expression based on your need
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_REGEX = "\\d{3}\\d{7}";
	private static final String NAME_REGEX = "^[a-zA-Z \\./-]*$";

	// Error Messages
	private static final String REQUIRED_MSG = "required";
	private static final String EMAIL_MSG = "invalid email";
	private static final String PHONE_MSG = "10-Digit";
	private static final String NAME_MSG = "Only Alphabets";

	// call this method when you need to check email validation
	public static boolean isEmailAddress(TextView dremail, boolean required) {
		return isValid1(dremail, EMAIL_REGEX, EMAIL_MSG, required);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////
	// call this method when you need to check phone number validation
	public static boolean isPhoneNumber(TextView editText, boolean required) {
		return isValid1(editText, PHONE_REGEX, PHONE_MSG, required);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	private static boolean isValid1(TextView dremail, String emailRegex,
			String emailMsg, boolean required) {
		// TODO Auto-generated method stub
		String text = dremail.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		dremail.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText1(dremail))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(emailRegex, text)) {
			dremail.setError(emailMsg);
			return false;
		}

        return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	private static boolean hasText1(TextView dremail) {
		// TODO Auto-generated method stub
		String text = dremail.getText().toString().trim();
		dremail.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			dremail.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// call this method when you need to check phone number validation
	public static boolean isName(TextView editText, boolean required) {
		return isValid2(editText, NAME_REGEX, NAME_MSG, required);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	private static boolean isValid2(TextView drname, String nameRegex,
			String emailMsg, boolean required) {
		// TODO Auto-generated method stub
		String text = drname.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		drname.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText1(drname))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(nameRegex, text)) {
			drname.setError(emailMsg);
			return false;
		}

        return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	private static boolean hasText11(TextView drname) {
		// TODO Auto-generated method stub
		String text = drname.getText().toString().trim();
		drname.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			drname.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////

	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(AppCompatEditText editText, String regex,
			String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(errMsg);
			return false;
		}

        return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// check the input field has any text or not
	// return true if it contains text otherwise false
	public static boolean hasText(AutoCompleteTextView editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

	public static boolean hasText(AppCompatEditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	public static boolean haspwd(TextView pwd) {

		String text = pwd.getText().toString().trim();
		pwd.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			pwd.setError(REQUIRED_MSG);
			// pwd.requestFocus();
			return false;
		}

		return true;
	}
	// //////////////////////////////////////////////////////////////////////////////////

}