package com.schlaf.steam.activities.importation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.android.common.view.DepthPageTransformer;
import com.example.android.common.view.SlidingTabLayout;
import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.selectlist.SelectedArmyFragment;
import com.schlaf.steam.storage.StorageManager;
import com.schlaf.steam.tabs.TabsAdapter;
import com.schlaf.steam.xml.TierExtractor;
import com.schlaf.steam.xml.XmlExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImportSelector extends ActionBarActivity implements ImportFileListener {

    TabHost tHost;

    TabsAdapter mTabsAdapter; // the adapter for swiping pages
    ViewPager pager; // the pager that handles fragments swipe

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_selector);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(3);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            pager.setPageTransformer(true, new DepthPageTransformer());
        }


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.LightGrey));

//		PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pagerTab);
//		tabStrip.setTabIndicatorColor(getResources().getColor(R.color.AndroidBlue));

        if (mTabsAdapter == null) {
            mTabsAdapter = new TabsAdapter(this, pager);
        }


        if (mTabsAdapter.getTabIndexForId(FilesToImportFragment.ID) == -1) {
            FilesToImportFragment filesToImportFragment = new FilesToImportFragment();
            mTabsAdapter.addTab(FilesToImportFragment.ID,  getResources().getString(R.string.import_files),
                    filesToImportFragment, null);
        }

        if (mTabsAdapter.getTabIndexForId(SelectedArmyFragment.ID) == -1) {
            ImportedFilesFragment importedFilesFragment = new ImportedFilesFragment();
            mTabsAdapter.addTab(ImportedFilesFragment.ID,  getResources().getString(R.string.files_imported),
                    importedFilesFragment, null);
        }

        if (mTabsAdapter.getTabIndexForId(ImportVersionsFragment.ID) == -1) {
            ImportVersionsFragment importVersionsFragment = new ImportVersionsFragment();
            mTabsAdapter.addTab(ImportVersionsFragment.ID,  getResources().getString(R.string.import_versions),
                    importVersionsFragment, null);
        }


        mTabsAdapter.notifyDataSetChanged();

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);

        
        getSupportActionBar().setTitle(R.string.import_data);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setLogo(R.drawable.import_content);
        
//        tHost = (TabHost) findViewById(android.R.id.tabhost);
//        tHost.setup();
//
//        /** Defining Tab Change Listener event. This is invoked when tab is changed */
//        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
//
//            @Override
//            public void onTabChanged(String tabId) {
//                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
//                FilesToImportFragment filesToImportFragment = (FilesToImportFragment) fm.findFragmentByTag(FilesToImportFragment.ID);
//                ImportedFilesFragment importedFilesFragment = (ImportedFilesFragment) fm.findFragmentByTag(ImportedFilesFragment.ID);
//                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
//
//                /** Detaches the toImport fragment if exists */
//                if(filesToImportFragment!=null)
//                    ft.detach(filesToImportFragment);
//
//                /** Detaches the imported fragment if exists */
//                if(importedFilesFragment!=null)
//                    ft.detach(importedFilesFragment);
//
//                /** If current tab is battles */
//                if(tabId.equalsIgnoreCase(FilesToImportFragment.ID)){
//
//                    if(filesToImportFragment==null){
//                        /** Create AndroidFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new FilesToImportFragment(), FilesToImportFragment.ID);
//                    }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(filesToImportFragment);
//                        // filesToImportFragment.refresh();
//                    }
//
//                }else{    /** If current tab is armies */
//                    if(importedFilesFragment==null){
//                        /** Create AppleFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new ImportedFilesFragment(), ImportedFilesFragment.ID);
//                     }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(importedFilesFragment);
//                    }
//                }
//                ft.commit();
//            }
//        };
//
//        /** Setting tabchangelistener for the tab */
//        tHost.setOnTabChangedListener(tabChangeListener);
//
//        /** Defining tab builder for armies tab */
//        TabHost.TabSpec tSpecFilesToImport = tHost.newTabSpec(FilesToImportFragment.ID);
//        tSpecFilesToImport.setIndicator(getResources().getString(R.string.import_files),getResources().getDrawable(R.drawable.import_content));
//        tSpecFilesToImport.setContent(new ImportTab(getBaseContext()));
//        tHost.addTab(tSpecFilesToImport);
//
//        /** Defining tab builder for battles tab */
//        TabHost.TabSpec tSpecFilesImported = tHost.newTabSpec(ImportedFilesFragment.ID);
//        tSpecFilesImported.setIndicator(getResources().getString(R.string.files_imported),getResources().getDrawable(R.drawable.edit_list_icon));
//        tSpecFilesImported.setContent(new ImportTab(getBaseContext()));
//        tHost.addTab(tSpecFilesImported);
 
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_import_help:
	    		LayoutInflater inflater = getLayoutInflater();
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    		alert.setTitle(R.string.import_how_to);
	    		
	    		View versionView = inflater.inflate(R.layout.import_how_to_layout, null);
	    		
	    	    WebView wvChanges= (WebView) versionView.findViewById(R.id.wvHowTo);
	    	    
	    	    try {
	                InputStream fin = getAssets().open("how_to_import.html");
	                    byte[] buffer = new byte[fin.available()];
	                    fin.read(buffer);
	                    fin.close();
	                    wvChanges.loadData(new String(buffer), "text/html", "UTF-8");
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	    	    
	    		alert.setView(versionView);
	    		alert.show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	    
    
  
	@Override
	public void onImportFileSelected(File file) {
		
		Toast.makeText(getApplicationContext(), R.string.starting_import_process_please_wait,
				Toast.LENGTH_SHORT).show();
		
		boolean success = true;
		// import
		Resources res = getResources();


		String extension = StorageManager.extractFileExtension(file);

		if (StorageManager.WHAC_EXTENSION.equalsIgnoreCase(extension)) {
			XmlExtractor extractor = new XmlExtractor(res,
					(SteamPunkRosterApplication) getApplication());
			// data file
			if (extractor.extractImportedFile(getApplication(), file)) {
				Toast.makeText(getApplicationContext(), R.string.import_successfull,
						Toast.LENGTH_SHORT).show();

				// if successfull, copy
				String fileName = file.getName();
				StorageManager.importDataFileFromFile(
						getApplicationContext(), fileName, file);

				// notify fragment...
                int index = mTabsAdapter.getTabIndexForId(ImportedFilesFragment.ID);
                ImportedFilesFragment fragment = (ImportedFilesFragment) mTabsAdapter.getItem(index);
				if (fragment != null) {
					fragment.notifyFileImported(file);
				}


			} else {
				success = false;
			}	
		} else if (StorageManager.TIER_EXTENSION.equalsIgnoreCase(extension)) {

			// tier file
			TierExtractor extractor = new TierExtractor(res,
					(SteamPunkRosterApplication) getApplication());

			if (extractor.extractImportedFile(getApplication(), file)) {
				Toast.makeText(getApplicationContext(), R.string.import_successfull,
						Toast.LENGTH_SHORT).show();

				// if successfull, copy
				String fileName = file.getName();
				StorageManager.importDataFileFromFile(
						getApplicationContext(), fileName, file);

				// notify fragment...
                int index = mTabsAdapter.getTabIndexForId(ImportedFilesFragment.ID);
                ImportedFilesFragment fragment = (ImportedFilesFragment) mTabsAdapter.getItem(index);
				if (fragment != null) {
					fragment.notifyFileImported(file);
				}

			} else {
				success = false;
			}
		} else {
			success = false;
		}

		if (!success) {
			Toast.makeText(getApplicationContext(),
					R.string.import_failed_make_sure_the_source_file_is_correct,
					Toast.LENGTH_SHORT).show();

		}

	}




	@Override
	public void onImportedFileDeleted(final File file) {
		// TODO Auto-generated method stub
   	Log.d("BattleSelector","onImportedFileDeleted " + file.getName());
    	
    	
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_the_file) + file.getName());
    	builder.setTitle(R.string.delete_file);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteImportedFile(getApplicationContext(), file)) {
                	// notify fragment...
                    int index = mTabsAdapter.getTabIndexForId(ImportedFilesFragment.ID);
                    ImportedFilesFragment fragment = (ImportedFilesFragment) mTabsAdapter.getItem(index);
                	fragment.notifyFileDeletion(file);
            	} else {
            		Toast.makeText(getApplicationContext(), R.string.deletion_failed, Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });
    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
	}

    @Override
    public void checkVersions(View v) {

        int index = mTabsAdapter.getTabIndexForId(ImportVersionsFragment.ID);
        ImportVersionsFragment fragment = (ImportVersionsFragment) mTabsAdapter.getItem(index);
        fragment.checkVersions();
    }


    @Override
	public void onImportFileDeleted(final File file) {
 	Log.d("BattleSelector","onImportFileDeleted " + file.getName());
    	
    	
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_the_file) + file.getName());
    	builder.setTitle(R.string.delete_file);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteImportedFile(getApplicationContext(), file)) {
                	// notify fragment...
                    int index = mTabsAdapter.getTabIndexForId(FilesToImportFragment.ID);
                    FilesToImportFragment fragment = (FilesToImportFragment) mTabsAdapter.getItem(index);
                	if (fragment != null) {
                		fragment.notifyFileDeletion(file);
                	}
            	} else {
            		Toast.makeText(getApplicationContext(), R.string.deletion_failed, Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });
    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();		
	}
}
