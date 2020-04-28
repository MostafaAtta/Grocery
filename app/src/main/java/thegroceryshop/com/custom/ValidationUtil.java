package thegroceryshop.com.custom;

import android.content.Context;
import android.location.LocationManager;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;


/**
 * Created by rohitg on 12/7/2016.
 */

public class ValidationUtil {

    private static final String REGEX_ONLY_NUMBERS = "[0-9]+";
    private static final String REGEX_FOR_VALID_AMOUNT = "^[1-9]\\d{0,6}(\\.\\d{1,2})?$";
    private static final String REGEX_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{6,20}$";


    /* *
      * method to validate the value of String
      *
      * @param string to be validate
      * @return true, if value is null or blank on "null" string.
      */
    public static boolean isNullOrBlank(Context context, String string) {
        if (string == null
                || string.equalsIgnoreCase(AppConstants.BLANK_STRING)
                || string.equalsIgnoreCase("null")) {
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_field), context.getString(R.string.ok));

            return true;
        }
        return false;
    }

    public static boolean isNullOrBlank(String string) {
        return string == null
                || string.equalsIgnoreCase(AppConstants.BLANK_STRING)
                || string.equalsIgnoreCase("null");
    }

    public static boolean isNullOrBlank(Context context, String string, View view, String type) {
        if (string == null || string.equalsIgnoreCase(AppConstants.BLANK_STRING) || string.equalsIgnoreCase("null")) {
            if (type.equalsIgnoreCase("firstName")) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_first_name_of_user));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_first_name_of_user), context.getString(R.string.ok), false);
            } else if (type.equalsIgnoreCase("lastName")) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_last_name_of_user));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_last_name_of_user), context.getString(R.string.ok), false);
            } else if (type.equalsIgnoreCase("Number")) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_phone));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_phone), context.getString(R.string.ok), false);
            } else if (type.equalsIgnoreCase("Email")) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_invalid_email));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_email), context.getString(R.string.ok), false);
            } else {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_name_of_user));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_name_of_user), context.getString(R.string.ok), false);

            }
            return true;
        }
        return false;
    }

   /* *
     * method to validate the value of CharSequence
     *
     * @param charSequence to be validate
     * @return true, if value is null or blank on "null" string.*/

    public static boolean isNullOrBlank(Object obj) {
        if(obj instanceof String || obj instanceof CharSequence){

            if(obj instanceof String){
                String string = (String)obj;
                return string.equalsIgnoreCase(AppConstants.BLANK_STRING) || string.equalsIgnoreCase("null");
            }

            CharSequence charSequence = (CharSequence)obj;
            return charSequence.toString().equalsIgnoreCase(AppConstants.BLANK_STRING) || charSequence.toString().equalsIgnoreCase("null");

        }else{
            return (obj == null);
        }
    }

    /*
     * method to validate digits string,
     *
     * @param string to be validate
     * @return true, if string contains only digits.*/

    public static boolean isOnlyDigits(Context context,String string) {

        if (isNullOrBlank(context,string))
            return false;
        else return string.matches(REGEX_ONLY_NUMBERS);

    }

    public static boolean isValidAmountDigits(Context context, String string) {

        if (isNullOrBlank(string)) {
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_amount), context.getString(R.string.ok));
            return false;
        } else if (string.matches(REGEX_FOR_VALID_AMOUNT))
            return true;
        else
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_amount), context.getString(R.string.ok));
            return false;
    }


    /*
     * Check whether the GPS provider of device is enabled or not
     *
     * @param context Context
     * @return true if GPS provider is enabled otherwise false.*/

    public static boolean isGPSEnabled(Context context) {
        boolean isGPSEnabled = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGPSEnabled = true;

        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGPSEnabled = false;
        }
        return isGPSEnabled;
    }

    /*
     * valid login
     *
     * @param context
     * @param edtTxtUserName
     * @return*/

    private static boolean isValidLogin(Context context, EditText edtTxtUserName) {

        boolean validation = true;
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(REGEX_EMAIL);
        Pattern Num_Chk = Pattern.compile(REGEX_ONLY_NUMBERS);
        String login_username = edtTxtUserName.getText().toString().trim();

        if (login_username.length() == 0) {

            validation = false;
            edtTxtUserName.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_username), context.getString(R.string.ok));

        } else if (login_username.length() < 5 && login_username.length() > 2 && login_username.matches(REGEX_ONLY_NUMBERS)) {

            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_short_phone), context.getString(R.string.ok));

        } else if (login_username.length() > 15 && login_username.matches(REGEX_ONLY_NUMBERS)) {

            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_long_phone), context.getString(R.string.ok));

        } else if (!EMAIL_ADDRESS_PATTERN.matcher(login_username).matches() && !login_username.matches(REGEX_ONLY_NUMBERS)) {

            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_email), context.getString(R.string.ok));
        } else if (login_username.length() < 2 && login_username.matches(REGEX_ONLY_NUMBERS)) {

            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_invalid_login_id), context.getString(R.string.ok));
        }

        return validation;

    }

    /**
     * Vaildation of RegisterOne Screen
     *
     * @param context
     * @return
     */

    public static boolean isValidRegisterOne(Context context,
                                             TextView txtViewCountryName,
                                             TextView txtViewCountryCode,
                                             EditText txtViewNumber){
        View views[] = new View[]{txtViewCountryName,txtViewCountryCode,txtViewNumber};

        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidPhoneNo(context, txtViewNumber, txtViewCountryCode.getText().toString());

    }

    /**
     * Vaildation of Forget Password Screen
     * @param context
     * @return
     */

    public static boolean isValidForgetPassword(Context context,
                                                EditText edtTextEmailForgotPassword){

        return NetworkUtil.networkStatus(context)
                && isNullOrBlank(context, edtTextEmailForgotPassword.getText().toString())
                && isValidEmail(context, edtTextEmailForgotPassword);

    }

    /**
     * Vaildation of Change Password Screen
     * @param context
     * @return
     */

    public static boolean isValidChangePassword(Context context,
                                                EditText edtTextOldPassword,EditText edtTextNewPassword){

View views[] = new View[]{edtTextOldPassword, edtTextNewPassword};

        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidPassword(context, edtTextNewPassword, false);

    }

    /**
     * Vaildation of Verify User Screen
     * @param context
     * @return
     */

    public static boolean isValidVerifyCode(Context context,
                                            EditText txtViewNumber){

        return NetworkUtil.networkStatus(context)
                && !isNullOrBlank(context, txtViewNumber.getText().toString());

    }

    public static boolean validateCreditCardForm(Context context, EditText mEdtUserName,
                                                 EditText mEdtCreditCardNumber,
                                                 EditText mEdtMM,
                                                 EditText mEdtYY, EditText mEdtCVV) {

        View[] views;

        try {
            views = new View[]{mEdtUserName, mEdtCreditCardNumber, mEdtMM, mEdtYY, mEdtCVV};
            return NetworkUtil.networkStatus(context)
                    && isAllFieldsBlank(context, views)
                    && isValidNameCardName(context, mEdtUserName, mEdtCreditCardNumber, mEdtMM, mEdtYY, mEdtCVV)
                    && isValidCreditCardNumber(context, mEdtCreditCardNumber, mEdtMM, mEdtYY, mEdtCVV)
                    && isValidMM(context, mEdtMM, mEdtYY)
                    && isValidYY(context, mEdtYY)
                    && isValidCVV(context, mEdtCVV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
        // && isValidCreditCardNumber(context, mEdtCreditCardNumber)
    }

    /**
     * method to validate the MM string of Credit Card
     *
     * @param context context of the component
     * @return true if MM string is valid
     */

    private static boolean isValidMM(Context context, EditText mEdtMM, EditText mEdtYY) {
        boolean validation = true;
        //Pattern Month_Chk = Pattern.compile("([01-12])");
        String month = mEdtMM.getText().toString();
        if (month.isEmpty()) {
            validation = false;
            mEdtMM.clearFocus();
            mEdtMM.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.creditCardMMEmpty), context.getString(R.string.ok));
        }

        if (mEdtYY.getText().toString().isEmpty()) {
            mEdtYY.clearFocus();
            mEdtYY.setSelected(true);
        }
        return validation;
    }

    /*
     * method to validate the email address string
     *
     * @param context   context of the component
     * @param edt_email reference to a EditText to which value to be validate
     * @return true if email address is valid*/

    public static boolean isValidEmail(Context context, EditText edt_email) {
        boolean validation = true;

        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(REGEX_EMAIL);
        // EditText is for email

        if (edt_email.getText().toString().trim().length() == 0) {
            validation = false;
            edt_email.setSelected(true);
            edt_email.setActivated(true);
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_email), context.getString(R.string.ok), false);
        } else if (!EMAIL_ADDRESS_PATTERN.matcher(edt_email.getText().toString().trim()).matches()) {
            validation = false;
            edt_email.setSelected(true);
            edt_email.setActivated(true);
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_email), context.getString(R.string.ok), false);
        }

        return validation;
    }

    public static boolean isValidEmails(Context context, EditText edt_email) {
        boolean validation = true;

        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(REGEX_EMAIL);

        if (edt_email.getText().toString().trim().length() == 0) {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_email));
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_email), context.getString(R.string.ok), false);

        } else if (!EMAIL_ADDRESS_PATTERN.matcher(edt_email.getText().toString().trim()).matches()) {
            validation = false;
            edt_email.setSelected(true);
            edt_email.setActivated(true);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_invalid_email));
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_email), context.getString(R.string.ok), false);

        }
        return validation;
    }

    public static boolean isValidUsername(Context context, EditText edt_email) {
        boolean validation = true;

        String username = edt_email.getText().toString();

        if(isOnlyDigits(context, username)){

            if(username.length() < 6){
                validation = false;
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_short_mobile_no), context.getString(R.string.ok), false);
            } else if(username.length() > 15){
                validation = false;
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_long_mobile_no), context.getString(R.string.ok), false);
            }

        }else{

            Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(REGEX_EMAIL);

            if (edt_email.getText().toString().trim().length() == 0) {
                validation = false;
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_email), context.getString(R.string.ok), false);

            } else if (!EMAIL_ADDRESS_PATTERN.matcher(edt_email.getText().toString().trim()).matches()) {
                validation = false;
                edt_email.setSelected(true);
                edt_email.setActivated(true);
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_email), context.getString(R.string.ok), false);

            }/*else {
                validation = false;
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_username), context.getString(R.string.ok), false);
            }*/

        }
        return validation;
    }

    /*
     * method to validate mobile no.
     *
     * @param context     context of the component
     * @param edt_phoneNo reference to a EditText to which value to be validate
     * @return true if mobile no is valid
     * */

    public static boolean isValidPhoneNo(Context context, EditText edt_phoneNo, String country_code) {
        boolean validation = true;
        String contactNo = edt_phoneNo.getText().toString().trim();
        if (contactNo.length() == 0) {
            validation = false;
            edt_phoneNo.setSelected(true);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_phone));
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_phone), context.getString(R.string.ok), false);
        } else if (!isOnlyDigits(context, contactNo)) {
            validation = false;
            edt_phoneNo.setSelected(true);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_invalid_phone));
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_phone), context.getString(R.string.ok), false);
        } else if (country_code.equalsIgnoreCase("65")) {
            if (contactNo.length() != 8) {
                validation = false;
                edt_phoneNo.setSelected(true);

                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_number_for_country), context.getString(R.string.ok), false);
            }else{
                edt_phoneNo.setSelected(false);
                validation = true;
            }
        }else if (!country_code.equalsIgnoreCase("65")) {
            if (contactNo.length() < 6 || contactNo.length() > 13) {
                validation = false;
                edt_phoneNo.setSelected(true);
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_invalid_international_number), context.getString(R.string.ok), false);
            }else{
                edt_phoneNo.setSelected(false);
                validation = true;
            }
        }
        return validation;
    }

    /*
     * method to validate the password string
     *
     * @param context      context of the component
     * @param edt_password reference to a EditText to which value to be validate
     * @return true if password string is valid
     * */

    public static boolean isValidPassword(Context context, EditText edt_password, boolean isForLogin) {
        boolean validation = true;
        String password = edt_password.getText().toString();
        Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile(REGEX_PASSWORD);
        Matcher matcher = FLIGHT_NUMBER_PATTERN.matcher(password);
        if (password.length() <= 0) {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_password));
        } else if (password.length() < 6) {
            if (!isForLogin) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_short_password));
                validation = false;
            } else {
                validation = true;
            }
        } else if (password.length() > 20) {
            if (!isForLogin) {
                validation = false;
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_long_password));

            } else {
                validation = true;
            }
        }
        return validation;
    }


     /*
     * method to validate the new password string
     *
     * @param context      context of the component
     * @param edt_password reference to a EditText to which value to be validate
     * @return true if password string is valid
     * */

    public static boolean isValidNewPassword(Context context, EditText edt_password, boolean isForLogin) {
        boolean validation = true;
        String password = edt_password.getText().toString();
        Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile(REGEX_PASSWORD);
        Matcher matcher = FLIGHT_NUMBER_PATTERN.matcher(password);
        if (password.length() <= 0) {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.new_password_error));
        } else if (password.length() < 6) {
            if (!isForLogin) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_short_newpassword));
                validation = false;
            } else {
                validation = true;
            }
        } else if (password.length() > 20) {
            if (!isForLogin) {
                validation = false;
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_long_password));

            } else {
                validation = true;
            }
        }
        return validation;
    }

      /*
     * method to validate the new password string
     *
     * @param context      context of the component
     * @param edt_password reference to a EditText to which value to be validate
     * @return true if password string is valid
     * */

    public static boolean isValidConfirmPassword(Context context, EditText edt_password, boolean isForLogin) {
        boolean validation = true;
        String password = edt_password.getText().toString();
        Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile(REGEX_PASSWORD);
        Matcher matcher = FLIGHT_NUMBER_PATTERN.matcher(password);
        if (password.length() <= 0) {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.retype_password_error));
        } else if (password.length() < 6) {
            if (!isForLogin) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_short_retypepassword));
                validation = false;
            } else {
                validation = true;
            }
        } else if (password.length() > 20) {
            if (!isForLogin) {
                validation = false;
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_long_password));

            } else {
                validation = true;
            }
        }
        return validation;
    }



    /*
     * method to validate the password string
     *
     * @param context      context of the component
     * @param edt_password reference to a EditText to which value to be validate
     * @param password     password string to validate
     * @return true if password string is valid
     * */

    public static boolean isValidConfirmPassword(Context context, EditText edt_password, String password) {
        boolean validation = true;
        String cnfm_password = edt_password.getText().toString();
        if (edt_password.getText().toString().length() <= 0) {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_cnfm_password));
        } else if (!password.equalsIgnoreCase(cnfm_password)) {
            validation = false;
            edt_password.setSelected(true);
            edt_password.setActivated(true);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_cnfm_password_not_match));
        }
        return validation;
    }

    public static boolean isValidConfirmPassword1(Context context, EditText edt_password, String password) {
        boolean validation = true;
        String cnfm_password = edt_password.getText().toString();
        if (edt_password.getText().toString().length() <= 0)
        {
            validation = false;
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_cnfm_password));
        } else if (!password.equalsIgnoreCase(cnfm_password)) {
            validation = false;
            edt_password.setSelected(true);
            edt_password.setActivated(true);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.err_cnfm_password_not_match1));
        }else
        {
            edt_password.setSelected(false);
            edt_password.setActivated(false);
        }
        return validation;
    }

    /*
     * method to validate the zip code string
     *
     * @param context     context of the component
     * @param edt_zipcode reference to a EditText to which value to be validate
     * @return true if zip code string is valid
     * */

    public static boolean isValidZipCode(Context context, EditText edt_zipcode) {
        boolean validation = true;
        String zip_code = edt_zipcode.getText().toString().trim();
        if (zip_code.length() <= 0) {
            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_zip_code), context.getString(R.string.ok));
        }
        return validation;
    }


    /*
     * method to validate the zip code string
     *
     * @param context     context of the component
     * @param edt_zipcode reference to a EditText to which value to be validate
     * @return true if zip code string is valid
     * */

    public static boolean isValidZipCodeZeroLenth(Context context, EditText edt_zipcode) {
        boolean validation = true;
        String zip_code = edt_zipcode.getText().toString().trim();
        if (zip_code.length() == 0) {
            edt_zipcode.setSelected(false);
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_zip_code), context.getString(R.string.ok));
        }else if (zip_code.length() > 8){
            validation = false;
            edt_zipcode.setSelected(true);
          //  AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.err_zip_code), context.getString(R.string.ok), false);

        }
        return validation;
    }

    /*
     * method to validate the date of birth string
     *
     * @param context           context of the component
     * @param txt_date_of_birth reference to a TextView to which value to be validate
     * @return true if date of birth string is valid
     * */

    public static boolean isValidDateOfBirth(Context context, TextView txt_date_of_birth) {
        boolean validation = true;
        String date_of_birth = txt_date_of_birth.getText().toString().trim();
        if (date_of_birth.length() <= 0 || date_of_birth.equalsIgnoreCase(context.getString(R.string.date_of_birth).toUpperCase())) {
            validation = false;
            txt_date_of_birth.setSelected(false);
            AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_date_of_birth));
//            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_date_of_birth), context.getString(R.string.ok), false);
        }
        return validation;
    }

    public static boolean isValidDateOfBirth(Context context, TextView txt_ViewMM, TextView txt_ViewDD, TextView txt_ViewYYYY) {
        boolean validation = true;
       // String date_of_birth = txt_date_of_birth.getText().toString().trim();
        if(!isNullOrBlank(context,txt_ViewMM.getText().toString(),txt_ViewMM,"MM") &&
                !isNullOrBlank(context,txt_ViewDD.getText().toString(),txt_ViewDD,"MM") &&
                !isNullOrBlank(context,txt_ViewYYYY.getText().toString(),txt_ViewYYYY,"MM") ){
            validation = false;

              AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_date_of_birth), context.getString(R.string.ok),false);
        }
        return validation;
    }

    /*
     * method to validate the name string
     *
     * @param context    context of the component
     * @param mEditTetxt reference to a EditText to which value to be validate
     * @return true if name string is valid
     * */

    public static boolean isValidNameOfUser(Context context, EditText mEditTetxt,String type) {
        boolean validation = true;
        String name = mEditTetxt.getText().toString().trim();
        if (name.length() <= 0) {
            validation = false;
            mEditTetxt.clearFocus();
            mEditTetxt.setSelected(true);
            if (type.equalsIgnoreCase("firstName")) {
                AppUtil.showErrorDialog(context, context.getResources().getString(R.string.error_blank_first_name_of_user));
//                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_first_name_of_user), context.getString(R.string.ok), false);
            } else if (type.equalsIgnoreCase("lastName")) {
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_last_name_of_user), context.getString(R.string.ok), false);

            }else if(type.equalsIgnoreCase("userName")){
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_user_name_of_user), context.getString(R.string.ok), false);

            }else{
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_name_of_user), context.getString(R.string.ok), false);

            }

        } else if (name.length() < 3 && !type.equalsIgnoreCase("userName")) {
            validation = false;
            mEditTetxt.clearFocus();
            mEditTetxt.setSelected(true);

            if(type.equalsIgnoreCase("firstName")) {
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_short_first_name_of_user), context.getString(R.string.ok), false);
            }else if(type.equalsIgnoreCase("lastName")){
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_short_last_name_of_user), context.getString(R.string.ok), false);

            }else{
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_short_name_of_user), context.getString(R.string.ok), false);

            }

            }
        else if (name.length() < 6 && type.equalsIgnoreCase("userName")) {

                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_short_user_name_of_user), context.getString(R.string.ok), false);


        }

         else if (name.length() > 50) {
            validation = false;
            mEditTetxt.clearFocus();
            mEditTetxt.setSelected(true);

            if(type.equalsIgnoreCase("firstName")) {
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_long_first_name_of_user), context.getString(R.string.ok), false);
            }else if(type.equalsIgnoreCase("lastName")){
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_long_last_name_of_user), context.getString(R.string.ok), false);

            }else if(type.equalsIgnoreCase("userName")){
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_long_user_name_of_user), context.getString(R.string.ok), false);

            }else{
                AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_long_name_of_user), context.getString(R.string.ok), false);

            }



        }
        return validation;
    }

    private static boolean isValidNameCardName(Context context, EditText mEditTetxt, EditText mEditNumber, EditText mEditMM, EditText mEditYY, EditText mEditCVV) {
        boolean validation = true;
        String name = mEditTetxt.getText().toString().trim();
        if (name.length() <= 0) {
            validation = false;
            mEditTetxt.clearFocus();
            mEditTetxt.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.error_blank_name_of_card_holder), context.getString(R.string.ok), false);
        }

        if(mEditNumber.getText().toString().isEmpty()) {
            mEditNumber.clearFocus();
            mEditNumber.setSelected(true);
        }

        if(mEditMM.getText().toString().isEmpty()) {
            mEditMM.clearFocus();
            mEditMM.setSelected(true);
        }

        if(mEditYY.getText().toString().isEmpty()) {
            mEditYY.clearFocus();
            mEditYY.setSelected(true);
        }

        if (mEditCVV.getText().toString().isEmpty()) {
            mEditCVV.clearFocus();
            mEditCVV.setSelected(true);
        }

        return validation;
    }

    /*
     * method to check all value of the views of array are blank or not
     *
     * @param context context of the component
     * @param views   array of views
     * @return false if all the values are blank
     * */

    public static boolean isAllFieldsBlank(Context context, View[] views) {

        boolean validation = true;
        int validation_count = 0;

        for (View view : views) {
            view.setSelected(false);
        }

        for (View view : views) {

            String value = AppConstants.BLANK_STRING;
            if (view instanceof EditText) {
                value = ((EditText) view).getText().toString();
            }else  if (view instanceof AppCompatEditText) {
                value = ((AppCompatEditText) view).getText().toString();
            }
            else if (view instanceof TextView) {
                value = ((TextView) view).getText().toString();
             /*   if(value.equalsIgnoreCase(context.getString(R.string.pickup_date))
                        || value.equalsIgnoreCase(context.getString(R.string.pickup_time))
                        || value.equalsIgnoreCase(context.getString(R.string.date_of_birth))){
                    value = AppConstants.BLANK_STRING;
                    view.setSelected(true);
                }*/
            } else if (view instanceof Spinner) {
              /*  if (((Spinner) view).getAdapter() instanceof SpinnerItemAdapter) {
                    int position = ((Spinner) view).getSelectedItemPosition();
                    if (position == 0) {
                        value = AppConstants.BLANK_STRING;
                        view.setSelected(true);
                    } else {
                        value = ((Spinner) view).getSelectedItem().toString();
                    }
                }else if(((Spinner) view).getAdapter() instanceof VehicleAdapter){
                    int position = ((Spinner) view).getSelectedItemPosition();
                    if (position == 0) {
                        value = AppConstants.BLANK_STRING;
                        view.setSelected(true);
                    } else {
                        value = ((Spinner) view).getSelectedItem().toString();
                    }
                }*/

            }

            if (value.equalsIgnoreCase(AppConstants.BLANK_STRING)) {
                validation_count++;
            }
        }

        // Check if validation_count is same as length of views
        if (validation_count == views.length) {

            // All fields are blank
            validation = false;
            for (View view : views) {

                if(view instanceof EditText && view.getTag() != null && view.getTag().toString() != null && view.getTag().toString().equalsIgnoreCase("zip")){

                }else{
                    view.setSelected(true);
                }
                view.clearFocus();
                //view.setFocusableInTouchMode(false);
            }
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.all_field_correctly), context.getString(R.string.ok));

        } else {
            for (View view : views) {
                if(view instanceof EditText) {
                    EditText editText   = (EditText) view;
                    if(editText.getText().toString().isEmpty()) {
                        view.clearFocus();

                        if(view instanceof EditText && view.getTag() != null && view.getTag().toString() != null && view.getTag().toString().equalsIgnoreCase("zip")){

                        }else{
                            view.setSelected(true);
                        }
                    }
                }
            }
            // Not all the Fields Blank
            validation = true;
        }

        return validation;
    }

    /*
     * method to validate the LOGIN form
     *
     * @param context context of the component
     * @return true if LOGIN fields have no issue
     * */

    public static boolean validateLoginForm(Context context, EditText mEdtUserName, EditText mEdtPassword) {

        View[] views;

        views = new View[]{mEdtUserName, mEdtPassword};

        // && isValidLogin(context, mEdtUserName)
        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                // && isValidLogin(context, mEdtUserName)
                && isValidPassword(context, mEdtPassword, true);
    }

   /*
     * method to validate the REGISTRATION form
     *
     * @param context context of the component
     * @param edt_zip_code
     * @return true if REGISTRATION fields have no issue
     * */

    public static boolean validateRegistrationForm(
            Context context,
            EditText mEdtFirstName,
            EditText mEdtLastName,
            EditText mEdtUserName,
            TextView mTxtDateOfBirth,
            EditText mEdtEmailAddress,
            EditText mEdtPassword){


        View[] views;

        views = new View[]{mEdtFirstName, mEdtLastName, mEdtUserName,mTxtDateOfBirth,mEdtEmailAddress, mEdtPassword };


        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidNameOfUser(context, mEdtFirstName, "firstName")
                && isValidNameOfUser(context, mEdtLastName, "lastName")
                && isValidNameOfUser(context, mEdtUserName, "userName")
                && isValidDateOfBirth(context, mTxtDateOfBirth)
                && isValidEmail(context, mEdtEmailAddress)
                && isRegex(context, mEdtPassword);
    }

    /*
     * method to validate the SOCIAL REGISTRATION form
     *
     * @param context context of the component
     * @return true if SOCIAL REGISTRATION fields have no issue
     * */

    public static boolean validateSocialRegistrationForm(
            Context context,
            EditText mEdtFullName,
            String countru_code,
            EditText mEdtMobileNo,
            EditText mEdtEmailAddress,
            TextView mTxtDateOfBirth) {

        View[] views;

        views = new View[]{mEdtFullName, mEdtMobileNo, mEdtEmailAddress, mTxtDateOfBirth};

        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidNameOfUser(context, mEdtFullName, "")
                && isValidPhoneNo(context, mEdtMobileNo, countru_code)
                && isValidEmail(context, mEdtEmailAddress)
                && isValidDateOfBirth(context, mTxtDateOfBirth);
    }


    /*
     * method to validate the MyProfile form
     *
     * @param context context of the component
     * @return true if MyProfile fields have no issue
     * */

    public static boolean validateMyProfileForm(
            Context context,
            EditText mEdtFullName,
            String country_code,
            EditText mEdtMobileNo,
            EditText mEdtEmailAddress,
            TextView mTxtDateOfBirth,
            EditText reg_edt_zipCode) {

        View[] views;

        views = new View[]{mEdtFullName, mEdtMobileNo, mEdtEmailAddress, mTxtDateOfBirth, reg_edt_zipCode};

        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidNameOfUser(context, mEdtFullName, "")
                && isValidPhoneNo(context, mEdtMobileNo, country_code)
                && isValidEmail(context, mEdtEmailAddress)
                && isValidDateOfBirth(context, mTxtDateOfBirth)
                && isValidZipCodeZeroLenth(context, reg_edt_zipCode);
    }

   /*
    ********************************************ADD CREDIT CARD FORM VALIDATION ***********************************

    *
     * method to validate the credit card form
     *
     * @param context context of the component
     * @return true if credit card fields have no issue
     * */


  /*  public static boolean validateCreditCardForm(Context context, EditText mEdtUserName,
                                                 EditText mEdtCreditCardNumber,
                                                 EditText mEdtMM,
                                                 EditText mEdtYY) {

        View[] views;

        try {
            views = new View[]{mEdtUserName, mEdtCreditCardNumber, mEdtMM, mEdtYY};
            return NetworkUtil.networkStatus(context)
                    && isAllFieldsBlank(context, views)
                    && isValidNameCardName(context, mEdtUserName, mEdtCreditCardNumber, mEdtMM, mEdtYY)
                    && isValidCreditCardNumber(context, mEdtCreditCardNumber, mEdtMM, mEdtYY)
                  *//*  && isValidMM(context, mEdtMM, mEdtYY)
                    && isValidYY(context, mEdtYY);*//*
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
        // && isValidCreditCardNumber(context, mEdtCreditCardNumber)
    }*/

    /*
     * method to validate the CVV string
     *
     * @param context context of the component
     * @return true if CVV string is valid
     * */


    public static boolean isValidCVV(Context context, EditText mEdtCVV) {

        if (mEdtCVV.getText().toString().isEmpty()) {
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.creditCardCVVEmpty), context.getString(R.string.ok));
            return false;
        }
        if (mEdtCVV.getText().toString().length() != 3) {
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.creditCardCVV_minLength), context.getString(R.string.ok));
            return false;
        } else {
            return true;
        }


    }

   /*
     * method to validate the YY string of Credit Card
     *
     * @param context context of the component
     * @return true if YY string is valid
     */

    private static boolean isValidYY(Context context, EditText mEdtYY) {
        boolean validation = true;
        String year = mEdtYY.getText().toString();
        if (year.isEmpty()) {
            mEdtYY.clearFocus();
            mEdtYY.setSelected(true);
            validation = false;
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.creditCardYYEmpty), context.getString(R.string.ok));

        }
        return validation;
    }

   /*
     * method to validate the CreditCardNumber string
     *
     * @param context context of the component
     * @return true if CreditCardNumber string is valid
     * */


    private static boolean isValidCreditCardNumber(Context context, EditText mEdtCreditCardNumber, EditText mEdtMM, EditText mEdtYY, EditText mEdtCVV) {
        boolean validation = true;

        if (mEdtCreditCardNumber.length() == 0) {
            mEdtCreditCardNumber.clearFocus();
            mEdtCreditCardNumber.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getResources().getString(R.string.creditCardNumberEmpty), context.getString(R.string.ok));
            validation = false;
        }

        if(mEdtMM.getText().toString().isEmpty()) {
            mEdtMM.clearFocus();
            mEdtMM.setSelected(true);
        }

        if(mEdtYY.getText().toString().isEmpty()) {
            mEdtYY.clearFocus();
            mEdtYY.setSelected(true);
        }

        if (mEdtCVV.getText().toString().isEmpty()) {
            mEdtCVV.clearFocus();
            mEdtCVV.setSelected(true);
        }
        return validation;

    }

/*
    ********************************************SetPassword FORM VALIDATION ***********************************

    *
     * method to validate the SetPassword form
     *
     * @param context context of the component
     * @return true if SetPassword fields have no issue
     * */

    public static boolean validateSetPasswordForm(Context context, EditText mEdtCurrentPassword, EditText mEdtNewPassword, EditText mConfirmEdtPassword) {

        View[] views;

        views = new View[]{mEdtCurrentPassword, mEdtNewPassword, mConfirmEdtPassword};
        return isAllFieldsBlank(context, views)
                && isValidPasswordWithConfrimPassword(context, mEdtNewPassword, mConfirmEdtPassword)
                && isValidPassword(context, mEdtNewPassword, true);
    }

    public static boolean isRegex(Context context, EditText mEdtNewPassword) {
        boolean validation = true;
        String password = mEdtNewPassword.getText().toString();
        Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile(REGEX_PASSWORD);
        Matcher matcher = FLIGHT_NUMBER_PATTERN.matcher(password);
        if (password.length() <= 0) {
            validation = false;
            mEdtNewPassword.clearFocus();
            mEdtNewPassword.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_password), context.getString(R.string.ok), false);
        } else if (password.length() < 6) {
            validation = false;
            mEdtNewPassword.clearFocus();
            mEdtNewPassword.setSelected(true);
            AppUtil.displaySingleActionAlert(context, "Weak Password", context.getString(R.string.err_short_password), context.getString(R.string.ok), false);
        } else if (password.length() > 20) {
            validation = false;
            mEdtNewPassword.clearFocus();
            mEdtNewPassword.setSelected(true);
            AppUtil.displaySingleActionAlert(context, "Weak Password", context.getString(R.string.err_long_password), context.getString(R.string.ok), false);
        } else if (!matcher.find()) {
            validation = false;
            mEdtNewPassword.clearFocus();
            mEdtNewPassword.setSelected(true);
            mEdtNewPassword.setText(AppConstants.BLANK_STRING);
            AppUtil.displaySingleActionAlert(context, "Weak Password", context.getString(R.string.err_password), context.getString(R.string.ok));
        }
        return validation;
    }


    /*
     * method to validate the password string
     *
     * @param context      context of the component
     * @param edt_password reference to a EditText to which value to be validate
     * @return true if password string is valid
     * */

    public static boolean isValidPasswordWithConfrimPassword(Context context, EditText edt_password, EditText edt_confirm_password) {
        boolean validation = true;
        String mConfirmPassword = edt_confirm_password.getText().toString().trim();
        String mPassword = edt_password.getText().toString();
        if (!mConfirmPassword.equals(mPassword)) {
            validation = false;
            edt_password.clearFocus();
            edt_password.setSelected(true);

            edt_confirm_password.clearFocus();
            edt_confirm_password.setSelected(true);
            AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, "The password and confirm password does not match.", context.getString(R.string.ok));
        }
        return validation;
    }



    /*
     * method to validate the LOGIN form
     *
     * @param context context of the component
     * @return true if LOGIN fields have no issue
     * */

    public static boolean validateForgotForm(Context context,EditText mEdtEmail) {

        View[] views;

        views = new View[]{mEdtEmail};

        return NetworkUtil.networkStatus(context)
                && isAllFieldsBlank(context, views)
                && isValidEmail(context, mEdtEmail);
    }




/*
    ********************************************Change Password FORM VALIDATION ***********************************

    *
     * method to validate the SetPassword form
     *
     * @param context context of the component
     * @return true if SetPassword fields have no issue
     * */

    public static boolean validateChangePasswordForm(Context context, EditText mEdtCurrentPassword, EditText mEdtNewPassword, EditText mConfirmEdtPassword) {

        View[] views;

        views = new View[]{mEdtCurrentPassword, mEdtNewPassword, mConfirmEdtPassword};
        return isAllFieldsBlank(context, views)
                && isValidPasswordWithConfrimPassword(context, mEdtNewPassword, mConfirmEdtPassword)
                && isRegex(context, mEdtNewPassword);
    }


}
