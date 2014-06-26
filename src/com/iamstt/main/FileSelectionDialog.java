package com.iamstt.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FileSelectionDialog
		implements OnItemClickListener
{
	private MainActivity					ACT;				// 呼び出し元
	private AlertDialog				m_dlg;					// ダイアログ
	private FileInfoArrayAdapter	m_fileinfoarrayadapter; // ファイル情報配列アダプタ
	private String[]				m_astrExt;				// フィルタ拡張子配列
	private Builder builder;
	private ListView listview;
	private boolean isRight;
	// コンストラクタ
	public FileSelectionDialog( MainActivity act,
								String strExt, boolean isRight )
	{
		ACT = act;
		this.isRight = isRight;
		// 拡張子フィルタ
		if( null != strExt )
		{
			StringTokenizer tokenizer = new StringTokenizer( strExt, ";" );
			int iCountToken = 0;
			while( tokenizer.hasMoreTokens() )
			{
				tokenizer.nextToken();
				iCountToken++;
			}
			if( 0 != iCountToken )
			{
				m_astrExt = new String[iCountToken];
				tokenizer = new StringTokenizer( strExt, ";" );
				iCountToken = 0;
				while( tokenizer.hasMoreTokens() )
				{
					m_astrExt[iCountToken] = tokenizer.nextToken();
					iCountToken++;
				}
			}
		}
	}

	// ダイアログの作成と表示
	public void show( File fileDirectory )
	{
		// タイトル
		String strTitle = fileDirectory.getAbsolutePath();

		// リストビュー
		listview = new ListView( ACT );
		listview.setScrollingCacheEnabled( false );
		listview.setOnItemClickListener( this );
		// ファイルリスト
		File[] aFile = fileDirectory.listFiles( getFileFilter() );
		List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
		if( null != aFile )
		{
			for( File fileTemp : aFile )
			{
				listFileInfo.add( new FileInfo( fileTemp.getName(), fileTemp ) );
			}
			Collections.sort( listFileInfo );
		}
		// 親フォルダに戻るパスの追加
		if( null != fileDirectory.getParent() )
		{
			listFileInfo.add( 0, new FileInfo( "..", new File( fileDirectory.getParent() ) ) );
		}
		m_fileinfoarrayadapter = new FileInfoArrayAdapter( ACT, listFileInfo );
		m_fileinfoarrayadapter.setListFileInfo(listFileInfo);
		for ( int i = 0 ; i < listFileInfo.size();i++){
			m_fileinfoarrayadapter.add(listFileInfo.get(i));
		}
		listview.setAdapter( m_fileinfoarrayadapter );

		builder = new AlertDialog.Builder( ACT );
		builder.setTitle( strTitle );
		builder.setView( listview );
		m_dlg = builder.show();
	}


	// ダイアログの作成と表示
	public void move( File fileDirectory )
	{
		// タイトル
		String strTitle = fileDirectory.getAbsolutePath();

		// ファイルリスト
		File[] aFile = fileDirectory.listFiles( getFileFilter() );
		List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
		if( null != aFile )
		{
			for( File fileTemp : aFile )
			{
				listFileInfo.add( new FileInfo( fileTemp.getName(), fileTemp ) );
			}
			Collections.sort( listFileInfo );
		}
		// 親フォルダに戻るパスの追加
		if( null != fileDirectory.getParent() )
		{
			listFileInfo.add( 0, new FileInfo( "..", new File( fileDirectory.getParent() ) ) );
		}
		m_fileinfoarrayadapter.clear();
		for ( int i = 0 ; i < listFileInfo.size();i++){
			m_fileinfoarrayadapter.add(listFileInfo.get(i));
		}
		m_fileinfoarrayadapter.setListFileInfo(listFileInfo);
		listview.setAdapter(m_fileinfoarrayadapter);
		builder.setTitle( strTitle );
	}

	// FileFilterオブジェクトの生成
	private FileFilter getFileFilter()
	{
		return new FileFilter()
		{
			public boolean accept( File arg0 )
			{
				if( null == m_astrExt )
				{ // フィルタしない
					return true;
				}
				if( arg0.isDirectory() )
				{ // ディレクトリのときは、true
					return true;
				}
				for( String strTemp : m_astrExt )
				{
					if( arg0.getName().toLowerCase().endsWith( "." + strTemp ) )
					{
						return true;
					}
				}
				return false;
			}
		};
	}



	// ListView内の項目をクリックしたときの処理
	public void onItemClick(	AdapterView<?> l,
								View v,
								int position,
								long id )
	{

		FileInfo fileinfo = m_fileinfoarrayadapter.getItem( position );

		if( true == fileinfo.getFile().isDirectory() )
		{
			move( fileinfo.getFile() );
		}
		else
		{
			if( null != m_dlg )
			{
				m_dlg.dismiss();
				m_dlg = null;
			}
			// ファイルが選ばれた：リスナーのハンドラを呼び出す
			ACT.onFileSelect( fileinfo.getFile() ,isRight);
		}
	}


private class FileInfoArrayAdapter extends ArrayAdapter<FileInfo>
{
	private List<FileInfo>	m_listFileInfo; // ファイル情報リスト

	// コンストラクタ
	public FileInfoArrayAdapter(	Context context,
									List<FileInfo> objects )
	{
		super(context,R.layout.file_list);

		m_listFileInfo = objects;
	}

	public void setListFileInfo(List<FileInfo> objects){
		this.m_listFileInfo = objects;
	}

	// m_listFileInfoの一要素の取得
	@Override
	public FileInfo getItem( int position )
	{
		return m_listFileInfo.get( position );
	}

	// 一要素のビューの生成
	@Override
	public View getView(	int position,
							View convertView,
							ViewGroup parent )
	{
		// レイアウトの生成
		if( null == convertView )
		{
			Context context = getContext();
			// レイアウト
			LinearLayout layout = (LinearLayout) LayoutInflater.from(ACT).inflate(R.layout.file_list, null);
			layout.setPadding( 10, 10, 10, 10 );
			layout.setBackgroundColor( Color.WHITE );
			convertView = layout;
			//アイコン
			ImageView iv = new ImageView(context);
			iv.setTag("icon");
			// テキスト
			TextView textview = new TextView( context );
			textview.setTag( "text" );
			textview.setTextColor( Color.BLACK );
			textview.setPadding( 10, 10, 10, 10 );
			layout.addView( iv );
			layout.addView( textview );
		}

		// 値の指定
		FileInfo fileinfo = m_listFileInfo.get( position );
		ImageView iv = (ImageView)convertView.findViewWithTag( "icon" );
		TextView textview = (TextView)convertView.findViewWithTag( "text" );
		if( fileinfo.getFile().isDirectory() )
		{ // ディレクトリの場合は、名前の後ろに「/」を付ける
			iv.setImageResource(R.drawable.folder);
			textview.setText( fileinfo.getName() + "/" );
		}
		else
		{
			if(fileinfo.getFile().getName().contains("mp3")){
			iv.setImageResource(R.drawable.music);
			}else{
				iv.setVisibility(View.GONE);
			}

			textview.setText( fileinfo.getName() );
		}

		return convertView;
	}
}

private class FileInfo
implements Comparable<FileInfo>
{
private String	m_strName;	// 表示名
private File	m_file;	// ファイルオブジェクト

// コンストラクタ
public FileInfo(	String strName,
				File file )
{
m_strName = strName;
m_file = file;
}

public String getName()
{
return m_strName;
}

public File getFile()
{
return m_file;
}

// 比較
public int compareTo( FileInfo another )
{
// ディレクトリ < ファイル の順
if( true == m_file.isDirectory() && false == another.getFile().isDirectory() )
{
	return -1;
}
if( false == m_file.isDirectory() && true == another.getFile().isDirectory() )
{
	return 1;
}

// ファイル同士、ディレクトリ同士の場合は、ファイル名（ディレクトリ名）の大文字小文字区別しない辞書順
return m_file.getName().toLowerCase().compareTo( another.getFile().getName().toLowerCase() );
}
}


}
