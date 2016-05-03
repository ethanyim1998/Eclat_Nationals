package ru.ifsoft.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import ru.ifsoft.network.adapter.ChatListAdapter;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;
import ru.ifsoft.network.dialogs.MsgImageChooseDialog;
import ru.ifsoft.network.model.ChatItem;
import ru.ifsoft.network.util.CustomRequest;

public class ChatFragment extends Fragment implements Constants {

    public final static int STATUS_START = 100;

    public final static String PARAM_TASK = "task";
    public final static String PARAM_STATUS = "status";

    public final static String BROADCAST_ACTION = "ru.ifsoft.network.chat";

    final String LOG_TAG = "myLogs";

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    Menu MainMenu;

    View mListViewHeader;

    RelativeLayout mLoadingScreen, mErrorScreen;
    LinearLayout mContentScreen, mContainerImg, mChatListViewHeaderContainer;

    ImageView mSendMessage, mAddImg, mEmojiBtn, mDeleteImg, mPreviewImg;
    EmojiconEditText mMessageText;

    ListView listView;

    BroadcastReceiver br;

    private ArrayList<ChatItem> chatList;

    private ChatListAdapter chatAdapter;

    String withProfile = "", messageText = "", messageImg = "";
    int chatId = 0, msgId = 0, messagesCount = 0, position = 0;
    long profileId = 0;

    private String selectedChatImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;

    int arrayLength = 0;
    Boolean loadingMore = false;
    Boolean viewMore = false;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;
    private Boolean visible = true;

    EmojiconsPopup popup;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();
        position = i.getIntExtra("position", 0);
        chatId = i.getIntExtra("chatId", 0);
        profileId = i.getLongExtra("profileId", 0);
        withProfile = i.getStringExtra("withProfile");

        chatList = new ArrayList<ChatItem>();
        chatAdapter = new ChatListAdapter(getActivity(), chatList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        popup = new EmojiconsPopup(rootView, getActivity());

        popup.setSizeForSoftKeyboard();

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mMessageText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mMessageText.dispatchKeyEvent(event);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                setIconEmojiKeyboard();
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {

                if (popup.isShowing())

                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mMessageText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mMessageText.dispatchKeyEvent(event);
            }
        });

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

        } else {

            App.getInstance().setCurrentChatId(chatId);

            restore = false;
            loading = false;
            preload = false;
        }

        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                int task = intent.getIntExtra(PARAM_TASK, 0);
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                Log.d(LOG_TAG, "onReceive: task = " + task + ", status = " + status);

                getNextMessages();
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        getActivity().registerReceiver(br, intFilt);

        if (loading) {

            showpDialog();
        }

        mLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.loadingScreen);
        mErrorScreen = (RelativeLayout) rootView.findViewById(R.id.errorScreen);

        mContentScreen = (LinearLayout) rootView.findViewById(R.id.contentScreen);

        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);
        mSendMessage = (ImageView) rootView.findViewById(R.id.sendMessage);
        mMessageText = (EmojiconEditText) rootView.findViewById(R.id.messageText);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newMessage();
            }
        });

        listView = (ListView) rootView.findViewById(R.id.listView);

        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        mListViewHeader = getActivity().getLayoutInflater().inflate(R.layout.chat_listview_header, null);
        mChatListViewHeaderContainer = (LinearLayout) mListViewHeader.findViewById(R.id.chatListViewHeaderContainer);

        listView.addHeaderView(mListViewHeader);

        mListViewHeader.setVisibility(View.GONE);

        listView.setAdapter(chatAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 && mListViewHeader.getVisibility() == View.VISIBLE) {

                    getPreviousMessages();
                }
            }
        });

        mAddImg = (ImageView) rootView.findViewById(R.id.addImg);
        mDeleteImg = (ImageView) rootView.findViewById(R.id.deleteImg);
        mPreviewImg = (ImageView) rootView.findViewById(R.id.previewImg);

        mContainerImg = (LinearLayout) rootView.findViewById(R.id.container_img);

        mContainerImg.setVisibility(View.GONE);

        mDeleteImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                selectedImage = null;
                selectedChatImg = "";

                mContainerImg.setVisibility(View.GONE);
            }
        });

        mAddImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                choiceImage();
            }
        });

        if (selectedChatImg != null && selectedChatImg.length() > 0) {

            mPreviewImg.setImageURI(Uri.fromFile(new File(selectedChatImg)));
            mContainerImg.setVisibility(View.VISIBLE);
        }

        if (!EMOJI_KEYBOARD) {

            mEmojiBtn.setVisibility(View.GONE);
        }

        mEmojiBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()){

                        popup.showAtBottom();
                        setIconSoftKeyboard();

                    } else {

                        mMessageText.setFocusableInTouchMode(true);
                        mMessageText.requestFocus();
                        popup.showAtBottomPending();

                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mMessageText, InputMethodManager.SHOW_IMPLICIT);
                        setIconSoftKeyboard();
                    }

                } else {

                    popup.dismiss();
                }
            }
        });

        EditTextImeBackListener er = new EditTextImeBackListener() {

            @Override
            public void onImeBack(EmojiconEditText ctrl, String text) {

                hideEmojiKeyboard();
            }
        };

        mMessageText.setOnEditTextImeBackListener(er);

        if (!restore) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getChat();

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (!preload) {

                    showContentScreen();

                } else {

                    showLoadingScreen();
                }

            } else {

                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void hideEmojiKeyboard() {

        popup.dismiss();
    }

    public void setIconEmojiKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_emoji);
    }

    public void setIconSoftKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_keyboard);
    }

    public void onDestroyView() {

        super.onDestroyView();

        getActivity().unregisterReceiver(br);

        hidepDialog();
    }

    @Override
    public void onResume() {

        super.onResume();

        visible = true;
    }

    @Override
    public void onPause() {

        super.onPause();

        visible = false;
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_CHAT_IMG && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            selectedChatImg = getImageUrlWithAuthority(getActivity(), selectedImage, "msg.jpg");

            try {

                selectedChatImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "msg.jpg";

                mPreviewImg.setImageURI(null);
                mPreviewImg.setImageURI(Uri.fromFile(new File(selectedChatImg)));

                mContainerImg.setVisibility(View.VISIBLE);

            } catch (Exception e) {

                Log.e("OnSelectPostImage", e.getMessage());
            }

        } else if (requestCode == CREATE_CHAT_IMG && resultCode == getActivity().RESULT_OK) {

            try {

                selectedChatImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "msg.jpg";

                mPreviewImg.setImageURI(null);
                mPreviewImg.setImageURI(Uri.fromFile(new File(selectedChatImg)));

                mContainerImg.setVisibility(View.VISIBLE);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        }
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri, String fileName) {

        InputStream is = null;

        if (uri.getAuthority() != null) {

            try {

                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);

                return writeToTempImageAndGetPathUri(context, bmp, fileName).toString();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (is != null) {

                        is.close();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage, String fileName) {

        String file_path = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER;
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);

        try {

            FileOutputStream fos = new FileOutputStream(file);

            inImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

            Toast.makeText(inContext, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "msg.jpg";
    }

    public void choiceImage() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        MsgImageChooseDialog alert = new MsgImageChooseDialog();

        alert.show(fm, "alert_dialog_image_choose");
    }

    public void imageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_CHAT_IMG);
    }

    public void imageFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "msg.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CREATE_CHAT_IMG);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollListViewToBottom() {

        listView.smoothScrollToPosition(chatAdapter.getCount());

        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }

    public void getChat() {

        preload = true;

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_CHAT_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

//                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();

                            if (!response.getBoolean("error")) {

                                msgId = response.getInt("msgId");
                                chatId = response.getInt("chatId");
                                messagesCount = response.getInt("messagesCount");

                                if (messagesCount > 20) {

                                    mListViewHeader.setVisibility(View.VISIBLE);
                                }

                                if (response.has("newMessagesCount")) {

                                    App.getInstance().setMessagesCount(response.getInt("newMessagesCount"));
                                }

                                if (response.has("messages")) {

                                    JSONArray messagesArray = response.getJSONArray("messages");

                                    arrayLength = messagesArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = messagesArray.length() - 1; i > -1; i--) {

                                            JSONObject msgObj = (JSONObject) messagesArray.get(i);

                                            ChatItem item = new ChatItem(msgObj);

                                            chatList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            showContentScreen();

                            chatAdapter.notifyDataSetChanged();

                            scrollListViewToBottom();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                preload = false;
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                params.put("profileId", Long.toString(profileId));

                params.put("chatId", Integer.toString(chatId));
                params.put("msgId", Integer.toString(msgId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getPreviousMessages() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_CHAT_GET_PREVIOUS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                msgId = response.getInt("msgId");
                                chatId = response.getInt("chatId");

                                if (response.has("messages")) {

                                    JSONArray messagesArray = response.getJSONArray("messages");

                                    arrayLength = messagesArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < messagesArray.length(); i++) {

                                            JSONObject msgObj = (JSONObject) messagesArray.get(i);

                                            ChatItem item = new ChatItem(msgObj);

                                            chatList.add(0, item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();

                            chatAdapter.notifyDataSetChanged();

                            if (messagesCount <= listView.getAdapter().getCount() - 1) {

                                mListViewHeader.setVisibility(View.GONE);

                            } else {

                                mListViewHeader.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                params.put("profileId", Long.toString(profileId));

                params.put("chatId", Integer.toString(chatId));
                params.put("msgId", Integer.toString(msgId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getNextMessages() {

        final ChatItem lastItem = (ChatItem) listView.getAdapter().getItem(listView.getAdapter().getCount() - 1);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_CHAT_GET_NEXT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("messages")) {

                                    JSONArray messagesArray = response.getJSONArray("messages");

                                    arrayLength = messagesArray.length();

                                    messagesCount = messagesCount + arrayLength;

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < messagesArray.length(); i++) {

                                            JSONObject msgObj = (JSONObject) messagesArray.get(i);

                                            ChatItem item = new ChatItem(msgObj);

                                            chatList.add(item);
                                        }

                                        if (!visible) {

                                            try {

                                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                                                r.play();

                                            } catch (Exception e) {

                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            chatAdapter.notifyDataSetChanged();

                            scrollListViewToBottom();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                params.put("profileId", Long.toString(profileId));

                params.put("chatId", Integer.toString(chatId));
                params.put("msgId", Integer.toString(lastItem.getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void newMessage() {

        if (App.getInstance().isConnected()) {

            messageText = mMessageText.getText().toString();
            messageText = messageText.trim();

            if (selectedChatImg.length() != 0) {

                loading = true;

                showpDialog();

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "msg.jpg");

                uploadFile(METHOD_MSG_UPLOAD_IMG, f);

            } else {

                if (messageText.length() > 0) {

                    loading = true;

                    showpDialog();

                    send();

                } else {

                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_msg), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

        } else {

            Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void send() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MSG_NEW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("message")) {

                                    JSONObject msgObj = (JSONObject) response.getJSONObject("message");

                                    ChatItem item = new ChatItem(msgObj);

                                    mContainerImg.setVisibility(View.GONE);
                                    selectedChatImg = "";
                                    selectedImage = null;
                                    messageImg = "";

                                    chatList.add(item);

                                    mMessageText.setText("");

                                    messagesCount++;

                                    chatAdapter.notifyDataSetChanged();

                                    scrollListViewToBottom();
                                }

                            } else {

                                Toast.makeText(getActivity(), getString(R.string.msg_send_msg_error), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                params.put("profileId", Long.toString(profileId));

                params.put("chatId", Integer.toString(chatId));
                params.put("messageText", messageText);
                params.put("messageImg", messageImg);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void deleteChat() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_CHAT_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                Intent i = new Intent();
                                i.putExtra("action", "Delete");
                                i.putExtra("position", position);
                                i.putExtra("chatId", chatId);
                                getActivity().setResult(RESULT_OK, i);

                                getActivity().finish();

//                                Toast.makeText(getActivity(), getString(R.string.msg_send_msg_error), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                params.put("profileId", Long.toString(profileId));
                params.put("chatId", Integer.toString(chatId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);

        mErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mErrorScreen.setVisibility(View.GONE);

        mContentScreen.setVisibility(View.VISIBLE);

        preload = false;

        getActivity().invalidateOptionsMenu();
    }

    private void showMenuItems(Menu menu, boolean visible) {

        for (int i = 0; i < menu.size(); i++){

            menu.getItem(i).setVisible(visible);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (App.getInstance().isConnected()) {

            if (!preload) {

                getActivity().setTitle(withProfile);

                showMenuItems(menu, true);

            } else {

                showMenuItems(menu, false);
            }

        } else {

            showMenuItems(menu, false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_chat, menu);

        MainMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_chat_delete: {

                deleteChat();

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public Boolean uploadFile(String serverURL, File file) {

        final OkHttpClient client = new OkHttpClient();

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                    loading = false;

                    hidepDialog();

                    Log.e("failure", request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    Log.e("response", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            messageImg = result.getString("imgUrl");
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        send();
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }
}