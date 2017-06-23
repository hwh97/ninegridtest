package com.android.ninegridtest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {


    public RecyclerView testRy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testRy=(RecyclerView)findViewById(R.id.testRy);
        testRy.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        Adapter adapter=new Adapter(this);
        testRy.setAdapter(adapter);
        new DownloadPicData(this,adapter).execute("http://cs.hwwwwh.cn/admin/testPic.php");
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        public View view;
        public LayoutInflater inflater;
        public Context context;
        public List<PicBean> list;

        public Adapter(Context context){
            this.context=context;
            inflater= LayoutInflater.from(context);
        }

        public void setList(List<PicBean> list) {
            this.list = list;
        }

        public List<PicBean> getList() {
            return list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view=inflater.inflate(R.layout.item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(list.get(position));
        }

        @Override
        public int getItemCount() {
            if(list!=null){
                return list.size();
            }
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }

            void bindData(PicBean data){
                NineGridPicLayout layout = (NineGridPicLayout) itemView.findViewById(R.id.nineGrid);
                List<PicBean> urlList = new ArrayList<>();//图片url
                if(data.Pic_url!=null) {
                    PicBean picBean = new PicBean();
                    picBean.setPic_url(data.getPic_url());
                    picBean.setPic_width(data.getPic_width());
                    picBean.setPic_height(data.getPic_height());
                    urlList.add(picBean);
                    layout.setIsShowAll(false); //当传入的图片数超过9张时，是否全部显示
                    layout.setSpacing(10); //动态设置图片之间的间隔
                    layout.setUrlList(urlList); //最后再设置图片url
                }else{
                    layout.setVisibility(View.GONE);
                }
            }
        }
    }

    class DownloadPicData extends AsyncTask<String,Void,List<PicBean>>{

        private Context context;
        private Adapter adapter;
        public DownloadPicData(Context context,Adapter adapter){
            this.context=context;
            this.adapter=adapter;
        }

        @Override
        protected List<PicBean> doInBackground(String... params) {
            List<PicBean> list=new ArrayList<>();
            String Url=params[0];
            if(HttpUtils.isNetConn(context)){
                byte[]b=null;
                b =HttpUtils.downloadFromNet(Url);
                String jsonString=new String(b);
                Log.d("DxtTag",jsonString);
                try {
                    JSONArray arr=new JSONArray(jsonString);
                    for (int i=0;i<arr.length();i++){
                        JSONObject obj=arr.getJSONObject(i);
                        PicBean picBean=new PicBean();
                        picBean.setPic_url(obj.getString("Pic_url"));
                        picBean.setPic_height(obj.getString("Pic_height"));
                        picBean.setPic_width(obj.getString("Pic_width"));
                        list.add(picBean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<PicBean> list) {
            super.onPostExecute(list);
            if(list!=null){
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(context,"error",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
