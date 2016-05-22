package com.hscompany.hstalk.TabFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hscompany.hstalk.MyPage;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.login.UserData;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kimsanghoon on 2015-08-05.
 */
public class FriendlistAdapter extends BaseAdapter implements Filterable
{
	final String ServerUrl = "http://54.238.209.107";

	private Context con;
	private LayoutInflater inflater;
	private ArrayList<UserData> userDataList; //이성 정보를 받아들일 리스트
	private ArrayList<UserData> saveList = null;
	private int layout;
	private myFilter filter;

	//사진관련 sample
	private URL url = null;
	private Bitmap bmp = null;
	private Bitmap defaultBmp = null;
	String surl;
	public FriendlistAdapter(Context context, int layout, final ArrayList<UserData> userDataList)
	{
		this.con = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.userDataList = userDataList;
		this.layout = layout;



	}

	@Override
	public int getCount()
	{
		return userDataList.size();
	}

	@Override
	public UserData getItem(int position)
	{
		return userDataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = inflater.inflate(layout, parent, false);
		}
		//사진관련 sample
		final ImageView img = (ImageView) convertView.findViewById((R.id.imageView));
		//img.setImageBitmap(getCircleBitmap(userDataList.get(position).getUserPic()));




		int userNum = userDataList.get(position).getUsernumber();

		if(userDataList.get(position).isHaspicture()==true)
		{
			surl = ServerUrl+"/picture/user/" + userNum + ".jpg";
			Glide.with(con)
					.load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
					.override(60,60)
					.signature(new StringSignature(""+userDataList.get(position).getPicchange()))
					.transform(new MyPage.CircleTransform(con.getApplicationContext()))
					.into(img);
		}
		else
		{
			if(userDataList.get(position).isSex()==true)
			{
				surl=ServerUrl+"/picture/user/default_man.jpg";
			} else
			{
				surl=ServerUrl+"/picture/user/default_woman.jpg";
			}
			Glide.with(con)
					.load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
					.override(60,60)
					.transform(new MyPage.CircleTransform(con.getApplicationContext()))
					.into(img);
		}



		//닉네임
		TextView txt1 = (TextView) convertView.findViewById(R.id.listitem_large_textView);
		txt1.setText(userDataList.get(position).getNickname());

		//성별표시
		boolean tmpsex=userDataList.get(position).isSex();

		TextView txt2 = (TextView) convertView.findViewById(R.id.listitem_small_textView1);
		if(tmpsex==true) //남성이면
			txt2.setText("남성");
		else
			txt2.setText("여성");

		return convertView;

	}

	@Override
	public Filter getFilter()
	{
		if(filter == null)
		{
			filter = new myFilter();
		}

		return filter;
	}

	public class myFilter extends Filter //실시간으로 검색 결과 보여주느 필터(닉네임검색)
	{
		protected FilterResults performFiltering(CharSequence constraint)
		{
			// TODO Auto-generated method stub

			if(saveList != null)
			{
				userDataList = saveList;
			}

			FilterResults Result = new FilterResults();
			// if constraint is empty return the original names
			if(constraint.length() == 0 )
			{
				Result.values = userDataList;
				Result.count = userDataList.size();
				return Result;
			}

			ArrayList<UserData> FilteredList = new ArrayList<UserData>();
			String filterString = constraint.toString().toLowerCase(); //검색하는단어
			String filterableString;

			for(int i = 0; i< userDataList.size(); i++)
			{
				filterableString = userDataList.get(i).getNickname(); //userDataList돌면서 검색하는단어가 포함되는 단어가 있는지 체크해서
				if(filterableString.toLowerCase().contains(filterString))
				{
					FilteredList.add(userDataList.get(i)); //FilteredList에 해당되는 내용 추가해줌
				}
			}
			Result.values = FilteredList;
			Result.count = FilteredList.size();

			saveList = userDataList;
			return Result;
		}

		@Override
		protected void publishResults(CharSequence constraint,FilterResults results)
		{
			// TODO Auto-generated method

			userDataList = (ArrayList<UserData>) results.values;
			notifyDataSetChanged();
		}

	}
}