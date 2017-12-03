package com.simonvn.nothinglab;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class MyMessageAdapter extends BaseAdapter   implements View.OnClickListener {
          
         /*********** Declare Used Variables *********/
         private Activity activity;
         private ArrayList data;
         private static LayoutInflater inflater=null;
         public Resources res;
         public String nUID;
         ChatMessage tempValues=null;
         int i=0;
          
         /*************  CustomAdapter Constructor *****************/
         public MyMessageAdapter(Activity a, ArrayList d, Resources resLocal, String n) {
              
                /********** Take passed values **********/
                 activity = a;
                 data = d;
                 res = resLocal;
                 nUID = n;
              
                 /***********  Layout inflator to call external xml layout () ***********/
                  inflater = (LayoutInflater)activity.
                                              getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              
         }
      
         /******** What is the size of Passed Arraylist Size ************/
         public int getCount() {
              
             if(data.size()<=0)
                 return 1;
             return data.size();
         }
      
         public Object getItem(int position) {
             return position;
         }
      
         public long getItemId(int position) {
             return position;
         }
          
         /********* Create a holder Class to contain inflated xml file elements *********/
         public static class ViewHolder{
              
             public TextView messageText; //text;
             public TextView messageUser; //text1;
             public TextView messageTime; //textWide;
             public CardView messageBG;
      
         }
      
         /****** Depends upon data size called for each row , Create each ListView row *****/
         public View getView(int position, View convertView, ViewGroup parent) {
              
             View vi = convertView;
             ViewHolder holder;
              
             if(convertView==null){

                 /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                 vi = inflater.inflate(R.layout.message, null);
                  
                 /****** View Holder Object to contain tabitem.xml file elements ******/

                 holder = new ViewHolder();
                 holder.messageText = (TextView) vi.findViewById(R.id.message_text);
                 holder.messageUser=(TextView)vi.findViewById(R.id.message_user);
                 holder.messageTime=(TextView)vi.findViewById(R.id.message_time);
                 holder.messageBG=(CardView) vi.findViewById(R.id.message_card);
                  
                /************  Set holder with LayoutInflater ************/
                 vi.setTag( holder );
             }
             else 
                 holder=(ViewHolder)vi.getTag();
              
             if(data.size()<=0)
             {
                 holder.messageText.setText("No Data");
                 holder.messageUser.setText("No Data");
                 holder.messageTime.setText("No Data");
                  
             }
             else
             {
                 /***** Get each Model object from Arraylist ********/
                 tempValues=null;
                 tempValues = ( ChatMessage ) data.get( position );
                  
                 /************  Set Model values in Holder elements ***********/


                 holder.messageText.setText(tempValues.getMessageText());
                 holder.messageUser.setText(tempValues.getMessageUser());
                 holder.messageUser.setHint(tempValues.getMessageUID());
                 if (Objects.equals(nUID, tempValues.getMessageUID())) {
                     holder.messageUser.setTextColor(holder.messageBG.getContext().getResources().getColor(R.color.colorWhite));
                     holder.messageText.setTextColor(holder.messageBG.getContext().getResources().getColor(R.color.colorWhite));
                     holder.messageTime.setTextColor(holder.messageBG.getContext().getResources().getColor(R.color.colorWhite));
                     holder.messageBG.setCardBackgroundColor(holder.messageBG.getContext().getResources().getColor(R.color.colorAccent));
                 }
                 holder.messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                         tempValues.getMessageTime()));

                  /******** Set Item Click Listner for LayoutInflater for each row *******/
 
                  vi.setOnClickListener(new OnItemClickListener( position ));
             }
             return vi;
         }
          
         @Override
         public void onClick(View v) {
                 //Log.v("CustomAdapter", "=====Row button clicked=====");
         }
          
         /********* Called when Item click in ListView ************/
         private class OnItemClickListener  implements View.OnClickListener {
             private int mPosition;
              
             OnItemClickListener(int position){
                  mPosition = position;
             }
              
             @Override
             public void onClick(View arg0) {
 
        
               MainActivity sct = (MainActivity)activity;
 
              /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
 
                 sct.onItemClick(mPosition);
             }              
         }  
     }