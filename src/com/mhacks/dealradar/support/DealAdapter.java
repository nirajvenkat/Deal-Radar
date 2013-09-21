package com.mhacks.dealradar.support;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mhacks.dealradar.Constants;
import com.mhacks.dealradar.DealRadar;
import com.mhacks.dealradar.FullScreenImageView;
import com.mhacks.dealradar.R;
import com.mhacks.dealradar.objects.Advertisement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sdickson on 9/21/13.
 */

public class DealAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Advertisement> deals;
    ProgressDialog progressDialog;

    public DealAdapter(Context context, ArrayList<Advertisement> deals)
    {
        this.context = context;
        this.deals = deals;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading Deal...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void setContent(ArrayList<Advertisement> deals)
    {
        this.deals = deals;
        notifyDataSetInvalidated();
    }

    public int getCount()
    {
        return deals.size();
    }

    public Object getItem(int position)
    {
        return deals.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.deal_list_row, parent, false);
        ImageView signalIndicator;
        TextView txtCompany, txtTitle;
        ImageView imgCategory = (ImageView) itemView.findViewById(R.id.deal_list_category_image);
        RelativeLayout rowLayout = (RelativeLayout) itemView.findViewById(R.id.deal_list_row_layout);
        signalIndicator = (ImageView) itemView.findViewById(R.id.deal_list_signal);
        txtCompany = (TextView) itemView.findViewById(R.id.deal_list_company);
        txtTitle = (TextView) itemView.findViewById(R.id.deal_list_title);
        txtTitle.setTypeface(DealRadar.myriadProRegular);
        txtCompany.setTypeface(DealRadar.myriadProSemiBold);

        Advertisement ad = deals.get(position);

        if(ad.company != null && !ad.company.isEmpty())
        {
            txtCompany.setText(ad.company);
        }
        else
        {
            txtCompany.setVisibility(View.GONE);
        }

        if(ad.title != null && !ad.title.isEmpty())
        {
            txtTitle.setText(ad.title);
        }
        else
        {
            txtTitle.setVisibility(View.GONE);
        }

        if(ad.signalStrength == Constants.SignalStrength.EXCELLENT)
        {

        }
        else if(ad.signalStrength == Constants.SignalStrength.GOOD)
        {

        }
        else if(ad.signalStrength == Constants.SignalStrength.FAIR)
        {

        }
        else
        {

        }

        if(ad.image_url != null)
        {
            rowLayout.setOnClickListener(new RowClickListener(ad));
        }

        if(ad.category != null && !ad.category.isEmpty())
        {
            int rsc = -1;

            if(ad.category.equalsIgnoreCase("Food"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/food_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Clothing"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/clothing_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Tech"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/tech_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Pets"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/pets_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Movies"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/movies_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Game"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/game_category", null, null);
            }
            else if(ad.category.equalsIgnoreCase("Toys"))
            {
                rsc = context.getResources().getIdentifier("com.mhacks.dealradar:drawable/toys_category", null, null);
            }

            if(rsc != -1)
            {
                imgCategory.setImageResource(rsc);
            }
        }
        else
        {
            imgCategory.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_right);
        itemView.startAnimation(animation);


        return itemView;
    }

    private class RowClickListener implements View.OnClickListener
    {
        Advertisement ad;

        public RowClickListener(Advertisement ad)
        {
            this.ad = ad;
        }

        public void onClick(View view)
        {
            new loadImageFromParse().execute(ad);
        }
    }

    private class loadImageFromParse extends AsyncTask<Object, Integer, Void>
    {
        Bitmap image;
        File compressed, file;
        Advertisement ad;

        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected void onProgressUpdate(Integer... progress)
        {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        protected Void doInBackground(Object... arg0)
        {
            try
            {
                ad = (Advertisement) arg0[0];
                URL url = new URL(ad.image_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();

                file = new File(context.getFilesDir() + "/" + ad.objectId + "_uncompressed");
                compressed = new File(context.getFilesDir() + "/" + ad.objectId);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                byte data[] = new byte[1024];

                int bytesRead = 0;
                while((bytesRead = is.read(data, 0, data.length)) >= 0)
                {
                    bos.write(data, 0, bytesRead);
                }

                bos.close();
                fos.close();
                is.close();

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(file),null,o);
                final int REQUIRED_SIZE=80;

                int width_tmp=o.outWidth, height_tmp=o.outHeight;
                int scale=1;
                while(true){
                    if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                        break;
                    width_tmp/=2;
                    height_tmp/=2;
                    scale*=2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=scale;
                image = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
                FileOutputStream out = new FileOutputStream(compressed);
                image.compress(Bitmap.CompressFormat.JPEG, 90, out);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            progressDialog.dismiss();

            if(image != null)
            {
                Intent intent = new Intent(context, FullScreenImageView.class);
                intent.putExtra("image", file.getAbsolutePath());
                intent.putExtra("caption", ad.title);
                context.startActivity(intent);

            }
        }
    }
}
