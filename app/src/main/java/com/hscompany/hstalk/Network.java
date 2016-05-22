package com.hscompany.hstalk;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Kimsanghoon on 2015-08-01.
 */
public class Network
{

	HttpURLConnection conn = null;
	URL url = null;
	BufferedReader br = null;
	String result;

	public String getData(String url)
	{
		try
		{
			this.url = new URL(url);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		try
		{
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				result = new String();
				for (; ; )
				{
					String line = br.readLine();
					if (line == null)
						break;
					result += line;
				}
				Log.d("Mylog", result);
				br.close();
				conn.disconnect();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}


	String sendDataGet(String url, String[][] sendData)
	{
		try
		{
			for(int i = 0 ; i < sendData.length ; i++)
			{
				if(i == 0)
					url += "?";
				else
					url += "&";
				try
				{
					url += URLEncoder.encode(sendData[i][0], "UTF-8") + "=" + URLEncoder.encode(sendData[i][1], "UTF-8");
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			this.url = new URL(url);
			Log.d("Mylog", this.url.toString());

		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		try
		{
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				result = new String();
				for (; ; )
				{
					String line = br.readLine();
					if (line == null)
						break;
					result += line;
				}

				br.close();
				conn.disconnect();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}


	public String sendDatePost(String url, String[][] sendData)
	{
		String str = null;

		try
		{
			this.url = new URL(url);
			Log.d("Mylog", this.url.toString());
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		String data = new String();

		try
		{
			for (int i = 0; i < sendData.length; i++)
			{
				if (i != 0)
				{
					data += "&";
				}
				data += URLEncoder.encode(sendData[i][0], "UTF-8") + "=" + URLEncoder.encode(sendData[i][1], "UTF-8");
			}
			Log.d("Mylog", data);
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		final String send = data;

		try
		{
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			AsyncTask async = new AsyncTask()
			{
				@Override
				protected Object doInBackground(Object[] params)
				{
					try
					{
						OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
						wr.write(send);
						wr.flush();
						wr.close();

						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
							result = new String();

							for (; ; )
							{
								String line = br.readLine();
								if (line == null)
									break;
								result += line;
							}

							br.close();
							conn.disconnect();
						}
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					return result;
				}
			};
			async.execute();

			try
			{
				str = async.get().toString();
			} catch (Exception e)
			{
				e.printStackTrace();
				return "Failed";
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return str;
	}

	String getUrl()
	{
		if (url == null)
			return null;
		else
			return this.url.toString();
	}

	public String sendDataAndPhoto(String url, String picPath, String[][] sendData)
	{
		String result = new String();
		if(picPath == null)
		{
			result = this.sendDatePost(url,sendData);
			return result;
		}

		try
		{
			this.url = new URL(url);
			Log.d("Mylog", this.url.toString());
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try
		{
			FileInputStream mFileInputStream = new FileInputStream(new File(picPath));

			// Open a HTTP  connection to  the URL

			conn = (HttpURLConnection) this.url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			for(int i = 0 ; i < sendData.length ; i++)
			{
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"" + sendData[i][0] + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
				byte[] data = sendData[i][1].getBytes("UTF-8");
				dos.write(data);
//				dos.writeBytes(sendData[i][1]);
				dos.writeBytes(lineEnd);
			}

			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			Log.d("Mylog", picPath);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ picPath + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024 * 1024 * 10;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Mylog", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0)
			{
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Mylog", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				// get response
				int ch;
				InputStream is = conn.getInputStream();
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1)
				{
					b.append((char) ch);
				}
				result = b.toString();
				Log.e("Mylog", "result = " + result);
				dos.close();
			}

		}catch(Exception e)
		{
			Log.d("Mylog", "exception " + e.getMessage());
			// TODO: handle exception
		}

		return result;
	}


	public void sendPhoto(String url, String fileName, String setFileName)
	{
		try
		{
			this.url = new URL(url);
			Log.d("Mylog", this.url.toString());
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try
		{

			FileInputStream mFileInputStream = new FileInputStream(new File(fileName));

			// Open a HTTP  connection to  the URL

			conn = (HttpURLConnection) this.url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", setFileName);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			Log.d("Mylog", setFileName);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ setFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024 * 1024 * 10;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Mylog", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0)
			{
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Mylog", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				// get response
				int ch;
				InputStream is = conn.getInputStream();
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1)
				{
					b.append((char) ch);
				}
				String s = b.toString();
				Log.e("Mylog", "result = " + s);
				dos.close();
			}

			}catch(Exception e)
			{
				Log.d("Mylog", "exception " + e.getMessage());
				// TODO: handle exception
			}
	}
}
