package com.example.jsonparser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static java.math.BigInteger.valueOf;

public class MainActivity extends AppCompatActivity {
    long prime = 908209935089L,radix= 256;
    long pHAsh_a = (long) (Math.random() * (prime))+1;
    long pHash_b = (long) (Math.random() * (prime + 1) );
    int m = 103650;
    JSONArray jsonArray;
    hash_obj[][] hashArray = new hash_obj[103650][];
    secondaryValues[] secondaryValuesSaved = new secondaryValues[103650];
    EditText search;
    TextView meaning;
    Button click ;
    String word=null;



    ArrayList<String> numberlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search =findViewById(R.id.search);
        meaning =findViewById(R.id.meaning);
        click = findViewById(R.id.clickk);
        try_hash();
        countSecHash(jsonArray);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word =search.getText().toString();
                int val= searchWord(word);
                String Peace = wordMeaning(val);
                System.out.println(Peace );
                meaning.setText(Peace);

            }
        });
    }


    public void try_hash() {
        jsonArray = get_json();

        JSONObject object = null;
        String meaning = "";
        try {
            object = jsonArray.getJSONObject(22);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            meaning = object.getString("en");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String enWord = null;
            try {
                enWord = obj.getString("en");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            long keyyy = countKey(enWord, 256);
            int hashValueKey = countPrimaryHash(keyyy);
            hash_obj[] hashValueKeyArr = hashArray[hashValueKey];
            if (hashValueKeyArr == null) {
                hashValueKeyArr = new hash_obj[1];
            }

            if (hashValueKeyArr[0] == null) {
                hashValueKeyArr[0] = new hash_obj(keyyy, i);
                hashArray[hashValueKey] = new hash_obj[1];
                hashArray[hashValueKey] = hashValueKeyArr;

            } else {

                ArrayList<hash_obj> hashValueKeyArraylist = new ArrayList<hash_obj>(Arrays.asList(hashValueKeyArr));
                hash_obj collidedObj = new hash_obj(keyyy, i);
                hashValueKeyArraylist.add(collidedObj);
                hashValueKeyArr = new hash_obj[hashValueKeyArraylist.size()];
                hashValueKeyArraylist.toArray(hashValueKeyArr);
                hashArray[hashValueKey] = null;
                hashArray[hashValueKey] = hashValueKeyArr;//103087

            }
        }
    }

    public void countSecHash(JSONArray jsonArray){
            for (int pSlot = 0; pSlot < jsonArray.length(); pSlot++) {
                boolean foundSecondaryhash = false;
                int arrLen = 0, squaredArr = 0;
                hash_obj[] temp = null;

                if (hashArray[pSlot] != null) {
                    temp = hashArray[pSlot];
                    arrLen = temp.length;
                }
                squaredArr = arrLen * arrLen;

                if (temp != null && temp.length > 1) {
                    while (foundSecondaryhash == false) {
                        //hashArray[pSlot] = null;
                        hashArray[pSlot] = new hash_obj[squaredArr];

                        long sHash_a = (long) (Math.random() * (prime)) + 1;
                        long sHash_b = (long) (Math.random() * (prime + 1));

                        secondaryValuesSaved[pSlot] = null;
                        secondaryValuesSaved[pSlot] = new secondaryValues(sHash_a, sHash_b, squaredArr);

                        for (int k = 0; k < temp.length; k++) {
                            int secondarySlot = secondaryValuesSaved[pSlot].countSecondHashValue(prime, temp[k].k);

                            if (hashArray[pSlot][secondarySlot] != null) {
                                break;
                            }
                            hashArray[pSlot][secondarySlot] = temp[k];
                            if (k == temp.length - 1) foundSecondaryhash = true;
                        }
                    }
                }
            }
        }
    
    public int searchWord(String s){
        int index;
        int countSHash=0;
        long k = countKey(s,radix);
        System.out.println();
        int countphash = countPrimaryHash(k);
        if(hashArray[countphash].length > 1 )
            countSHash = secondaryValuesSaved[countphash].countSecondHashValue(prime,k);
        index = hashArray[countphash][countSHash].val;
        return index;
    }
    public long countKey(String word,long radix){
        int len = word.length()-1;
        long key=0L;
        for (int i = 0 ; i<word.length(); i++){
            key = ( key*radix)%prime  + (word.charAt(i)%prime);
            len -=1;
        }
        return key;
    }
    public JSONArray get_json(){
        String json;
        JSONArray jsonArray = null;
        try {
            InputStream is = getAssets().open("E2Bdatabase.json");
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            jsonArray = new JSONArray(json);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
    public int countPrimaryHash(long keyyyyy){
        BigInteger ak = valueOf(( pHAsh_a*keyyyyy)%prime);
        BigInteger ak_b =ak.add(valueOf(pHash_b));
        BigInteger ak_b_modp =  ak.mod(BigInteger.valueOf(prime));
        int hashValue_of_Key = (ak_b_modp.mod(valueOf(m))).intValue();
        return hashValue_of_Key;
    }

    public String wordMeaning(int index) {
        JSONObject obj = null;
        try {
            obj = jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String meaning = null;
        try {
            meaning = obj.getString("bn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meaning;
    }

}