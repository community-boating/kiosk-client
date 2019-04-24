package com.example.alexbanks.cbiapp.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextDirectionHeuristic;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.AdminGUIActivity;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.input.CustomInputManager;
import com.example.alexbanks.cbiapp.input.listener.CustomInputProgressStateListener;
import com.example.alexbanks.cbiapp.input.listener.CustomInputTextWatcherListener;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValueValidator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CustomKeyboard extends Keyboard implements KeyboardView.OnKeyboardActionListener {

    public static final int KEY_CODE_DELETE = 55551;
    public static final int KEY_CODE_NEXT = 55552;
    public static final int KEY_CODE_SHIFT = 55553;

    public static final int KEYBOARD_MODE_FULL = 0;
    public static final int KEYBOARD_MODE_SIMPLE = 1;
    public static final int KEYBOARD_MODE_NUMBER_PAD = 2;

    private boolean shiftStatus=false;
    private int mode;

    //TODO probably not have this forever
    public static CustomKeyboard instance;

    private EnterListener enterListener;

    private Activity activity;
    private KeyboardView keyboardView;
    private List<View> textViews;
    public CustomKeyboard(Activity activity, int mode, int viewId){
        super(activity, mode==KEYBOARD_MODE_FULL?R.xml.custom_keyboard_full:mode==KEYBOARD_MODE_SIMPLE?R.xml.custom_keyboard:R.xml.custom_keypad);
        instance=this;
        this.activity = activity;
        this.mode=mode;
        keyboardView = activity.findViewById(viewId);
        keyboardView.setKeyboard(this);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        updateShiftStatus();
        textViews = new LinkedList<>();
        for(Key k : this.getKeys()){
            //k.popupResId = R.xml.custom_keypad_popup;
            if(k.codes == null)
                continue;
            Log.d("keyval", "value: " + k.label + ":" + k.codes.length + ":" + k.codes[0]);
        }
        initializeKeys();
    }
    public static final Rect minimumKeyboardRes=new Rect(0,0,800,400);
    private static DisplayMetrics displayMetrics = new DisplayMetrics();
    public boolean displayCustomKeyboard(Activity a){
        if(Build.VERSION.SDK_INT < 21)
            return false;
        a.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return !minimumKeyboardRes.contains(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }
    private void updateShiftStatus(){
        keyboardView.setShifted(shiftStatus);
    }
    private void initializeKeys(){
        StringBuffer buffer = new StringBuffer(2);
        for(Key k : this.getKeys()){
            if(k.codes == null)
                continue;
            if(k.codes.length == 1 && k.codes[0] >= 'a' && k.codes[0] <= 'z') {
                char c = (char) k.codes[0];
                char C = Character.toUpperCase(c);
                buffer.delete(0, 2);
                buffer.append(c);
                buffer.append(C);
                k.popupCharacters = buffer.toString();
                k.iconPreview=k.icon;
                k.popupResId = R.xml.custom_keypad_popup;
            }else {
                if(k.codes.length == 1 && k.codes[0] == KEY_CODE_SHIFT) {
                    k.iconPreview = null;
                    //k.popupResId = R.xml.custom_keypad_popup;
                    //k.popupCharacters="ab";
                }
                if(k.popupCharacters != null && k.popupCharacters.length() > 1)
                    k.popupResId=R.xml.custom_keypad_popup;
            }
        }
    }
    public void addTextViewsFromCustomInputManager(){
        for(CustomInputProgressStateListener valueValidator : CustomInputManager.getCustomInputs()){
            if(valueValidator instanceof CustomInputTextWatcherListener){
                CustomInputTextWatcherListener textValueValidator = (CustomInputTextWatcherListener)valueValidator;
                textViews.add(textValueValidator.getInputRef());
            }
        }
    }
    public void setEnterListener(EnterListener enterListener){
        this.enterListener=enterListener;
    }
    public void addTextViews(Collection<View> views){ textViews.addAll(views); }
    public void addTextView(View v){
        textViews.add(v);
    }
    public void clearViews(){
        textViews.clear();
    }
    public void setTextViewFocuses(){
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Log.d("derpderp", "derpderp : ");
                if(hasFocus){
                    handleFocusChanged(v);
                }
            }
        };
        for(View v : textViews){
            if(v instanceof EditText) {
                EditText editText = (EditText)v;
                editText.setOnFocusChangeListener(focusChangeListener);
            }
        }
    }
    public void handleFocusChanged(View v){
        EditText editText = (EditText)v;
        String text = editText.getText().toString();
        if(text==null||text.isEmpty() && (editText.getInputType() & InputType.TYPE_TEXT_VARIATION_PERSON_NAME) == InputType.TYPE_TEXT_VARIATION_PERSON_NAME){
            shiftStatus=true;
            updateShiftStatus();
        }else{
            shiftStatus=false;
            updateShiftStatus();
        }
    }
    public void hideSoftwareKeyboard(View v){
        ((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public void showCustomKeyboard(){
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
    }
    public void hideCustomKeyboard(){
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public boolean isCodePreview(int primaryCode){
        return (primaryCode == KEY_CODE_NEXT || primaryCode == KEY_CODE_SHIFT || primaryCode == KEY_CODE_DELETE) && this.mode == KEYBOARD_MODE_FULL;
    }

    @Override
    public void onPress(int primaryCode) {
        keyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        //Vibrator vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
        //if(vibrator.hasVibrator()){
        //    vibrator.vibrate(20);
        //}
        if(isCodePreview(primaryCode))
            keyboardView.setPreviewEnabled(false);
    }

    @Override
    public void onRelease(int primaryCode) {
        keyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        if(isCodePreview(primaryCode))
            keyboardView.setPreviewEnabled(true);
    }

    private int hashtags=0;

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //KeyEvent event = new KeyEvent() {};
        //manager.dispatchKeyEventFromInputMethod(this.keyboardView, event);
        View focusCurrent = activity.getWindow().getCurrentFocus();
        if(primaryCode == KEY_CODE_NEXT) {
            if(enterListener != null){
                if(enterListener.handleEnter())
                    return;
            }
            if(activity instanceof BaseActivity){
                if(focusCurrent!=null) {
                    if(focusCurrent instanceof EditText){
                        EditText editText = (EditText)focusCurrent;
                        Log.d("derp", "derpime : " + editText.getImeOptions());
                    }
                    int id = focusCurrent.getNextFocusForwardId();
                    if(id >= 0){
                        View nextFocus = activity.findViewById(id);
                        nextFocus.requestFocus();
                        return;
                    }else{
                        Log.d("derp", "not good");
                    }
                }
                BaseActivity baseActivity = (BaseActivity)activity;
                baseActivity.nextProgress();
                return;
            }
        }
        if(focusCurrent == null || !(focusCurrent instanceof EditText)) return;
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();
        boolean wasHashtag=false;
        if(primaryCode == KEY_CODE_DELETE){
            if(start > 0)
                editable.delete(start - 1, start);
        }else if(primaryCode == KEY_CODE_SHIFT){
            shiftStatus = !shiftStatus;
            updateShiftStatus();
        }else{
            char c = (char)primaryCode;
            //TODO janky code for admin panel access
            if(c == '#'){
                hashtags++;
                wasHashtag=true;
                if(hashtags>=5){
                    hashtags=0;
                    Intent adminIntent = new Intent(activity, AdminGUIActivity.class);
                    activity.startActivity(adminIntent);
                }
            }
            if((c >= 'a') && (c <= 'z') && shiftStatus) {
                c = Character.toUpperCase(c);
                shiftStatus=false;
                updateShiftStatus();
            }
            editable.insert(start, "" + c);
        }
        if(!wasHashtag)
            hashtags=0;
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public static interface EnterListener {
        public boolean handleEnter();
    }

}
