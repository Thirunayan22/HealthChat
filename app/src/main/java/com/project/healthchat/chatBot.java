package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.healthchat.Adapter.MessageAdapter;
import com.project.healthchat.model.ResponseMessage;

import java.util.ArrayList;
import java.util.List;

public class chatBot extends AppCompatActivity {

    EditText editText;
    Button sendBtn;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessagesList;
    final static  String URL = "https://6775853bd3cf.ngrok.io/chat?text=";
    public String botreply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        editText = findViewById(R.id.et_message);
        sendBtn = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.rv_messages);

        responseMessagesList = new ArrayList<>();
        messageAdapter  = new MessageAdapter(responseMessagesList,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);

        ResponseMessage starter = new ResponseMessage("Hi,how are you feeling today?",false);
        responseMessagesList.add(starter);
        messageAdapter.notifyDataSetChanged();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.setText("");
                String  message = editText.getText().toString();
                ResponseMessage responseMessage = new ResponseMessage(editText.getText().toString(),true);
                responseMessagesList.add(responseMessage);
                sendRequest(URL,message);






            }
        });
;






    }

    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");

            Log.e("Volley Error ", responseBody);
        } catch (Exception e) {
            Log.e("Parsing error","Volley");

        }


    }

    private String sendRequest(String url,final String message){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                botreply = response;
                ResponseMessage responseMessageReply = new ResponseMessage(botreply, false);
                responseMessagesList.add(responseMessageReply);
                messageAdapter.notifyDataSetChanged();
                if (!isLastVisible()) {
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                Log.e("Response from server", response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ResponseMessage responseMessageReply = new ResponseMessage("I am conntected online please retry after conntecting!", false);
                responseMessagesList.add(responseMessageReply);
                messageAdapter.notifyDataSetChanged();
                if (!isLastVisible()) {
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                Log.e("Response from server", "Conntection error");

                parseVolleyError(error);
            }
        });

        requestQueue.add(stringRequest);
        return botreply;
    }
}