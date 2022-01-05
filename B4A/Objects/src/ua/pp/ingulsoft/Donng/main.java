package ua.pp.ingulsoft.Donng;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "ua.pp.ingulsoft.Donng", "ua.pp.ingulsoft.Donng.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "ua.pp.ingulsoft.Donng", "ua.pp.ingulsoft.Donng.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "ua.pp.ingulsoft.Donng.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _vvv1 = null;
public static anywheresoftware.b4a.objects.Timer _v7 = null;
public static String _v0 = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblstart = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblstop = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmenu = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlmenu = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblback = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsettings = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblexit = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbackfromsettings = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsettingstitle = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblinterval = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmin = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _seekbarmin = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblsec = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _seekbarsec = null;
public anywheresoftware.b4a.audio.SoundPoolWrapper _v5 = null;
public static int _v6 = 0;
public static boolean _vv1 = false;
public static boolean _vv6 = false;
public static String _vv7 = "";
public static String _vv0 = "";

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 67;BA.debugLine="Log(\"+ Activity_Create +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("8131073","+ Activity_Create +",0);
 //BA.debugLineNum = 68;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 69;BA.debugLine="Log(\"*   First time *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("8131075","*   First time *",0);
 //BA.debugLineNum = 70;BA.debugLine="sp.Initialize(6)";
mostCurrent._v5.Initialize((int) (6));
 //BA.debugLineNum = 71;BA.debugLine="nDongId = sp.Load(File.DirAssets, \"121800__boss-";
_v6 = mostCurrent._v5.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"121800__boss-music__gong.wav");
 //BA.debugLineNum = 72;BA.debugLine="tmrTimer.Initialize(\"TickTimer\", nInterval)";
_v7.Initialize(processBA,"TickTimer",(long)(Double.parseDouble(_v0)));
 };
 //BA.debugLineNum = 75;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 76;BA.debugLine="bStarted = False";
_vv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 77;BA.debugLine="ToggleStartStopButtons";
_vv2();
 //BA.debugLineNum = 78;BA.debugLine="Log(\"+ Activity_Create +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("8131084","+ Activity_Create +",0);
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 81;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
public static String  _vv3() throws Exception{
 //BA.debugLineNum = 147;BA.debugLine="Private Sub Dong";
 //BA.debugLineNum = 148;BA.debugLine="sp.Play(nDongId, 1, 1, 1, 0, 1)";
mostCurrent._v5.Play(_v6,(float) (1),(float) (1),(int) (1),(int) (0),(float) (1));
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static int  _vv4() throws Exception{
int _itv = 0;
int _result = 0;
int _reminder = 0;
 //BA.debugLineNum = 190;BA.debugLine="Private Sub GetMinutes As Int";
 //BA.debugLineNum = 191;BA.debugLine="Dim itv As Int = nInterval / 1000";
_itv = (int) ((double)(Double.parseDouble(_v0))/(double)1000);
 //BA.debugLineNum = 192;BA.debugLine="Dim result As Int = 0";
_result = (int) (0);
 //BA.debugLineNum = 194;BA.debugLine="If itv > 60 Then";
if (_itv>60) { 
 //BA.debugLineNum = 195;BA.debugLine="Dim reminder As Int = itv Mod 60";
_reminder = (int) (_itv%60);
 //BA.debugLineNum = 196;BA.debugLine="result = (itv - reminder) /60";
_result = (int) ((_itv-_reminder)/(double)60);
 };
 //BA.debugLineNum = 198;BA.debugLine="Log(\"*   Minutes: '\" & result & \"' *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("836765704","*   Minutes: '"+BA.NumberToString(_result)+"' *",0);
 //BA.debugLineNum = 199;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return 0;
}
public static int  _vv5() throws Exception{
int _itv = 0;
int _result = 0;
int _reminder = 0;
 //BA.debugLineNum = 202;BA.debugLine="Private Sub GetSeconds As Int";
 //BA.debugLineNum = 203;BA.debugLine="Dim itv As Int = nInterval / 1000";
_itv = (int) ((double)(Double.parseDouble(_v0))/(double)1000);
 //BA.debugLineNum = 204;BA.debugLine="Dim result As Int = itv";
_result = _itv;
 //BA.debugLineNum = 206;BA.debugLine="If itv > 59 Then";
if (_itv>59) { 
 //BA.debugLineNum = 207;BA.debugLine="Dim reminder As Int = itv Mod 60";
_reminder = (int) (_itv%60);
 //BA.debugLineNum = 208;BA.debugLine="result = (itv - reminder) /60";
_result = (int) ((_itv-_reminder)/(double)60);
 };
 //BA.debugLineNum = 210;BA.debugLine="Log(\"*   Seconds: '\" & result & \"' *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("837289992","*   Seconds: '"+BA.NumberToString(_result)+"' *",0);
 //BA.debugLineNum = 211;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 212;BA.debugLine="End Sub";
return 0;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 32;BA.debugLine="Private lblStart As Label";
mostCurrent._lblstart = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private lblStop As Label";
mostCurrent._lblstop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private lblMenu As Label";
mostCurrent._lblmenu = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private pnlMenu As Panel";
mostCurrent._pnlmenu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private lblBack As Label";
mostCurrent._lblback = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private lblSettings As Label";
mostCurrent._lblsettings = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private lblExit As Label";
mostCurrent._lblexit = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private lblBackFromSettings As Label";
mostCurrent._lblbackfromsettings = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private lblSettingsTitle As Label";
mostCurrent._lblsettingstitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private lblInterval As Label";
mostCurrent._lblinterval = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private lblMin As Label";
mostCurrent._lblmin = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private seekBarMin As SeekBar";
mostCurrent._seekbarmin = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private lblSec As Label";
mostCurrent._lblsec = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private seekBarSec As SeekBar";
mostCurrent._seekbarsec = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private sp As SoundPool";
mostCurrent._v5 = new anywheresoftware.b4a.audio.SoundPoolWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private nDongId As Int";
_v6 = 0;
 //BA.debugLineNum = 59;BA.debugLine="Private bStarted As Boolean";
_vv1 = false;
 //BA.debugLineNum = 60;BA.debugLine="Private bMenuOpened As Boolean";
_vv6 = false;
 //BA.debugLineNum = 62;BA.debugLine="Private strMinText As String = \"Minutes: \"";
mostCurrent._vv7 = "Minutes: ";
 //BA.debugLineNum = 63;BA.debugLine="Private strSecText As String = \"Seconds: \"";
mostCurrent._vv0 = "Seconds: ";
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _lblback_click() throws Exception{
 //BA.debugLineNum = 185;BA.debugLine="Private Sub lblBack_Click";
 //BA.debugLineNum = 186;BA.debugLine="bMenuOpened = False";
_vv6 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 187;BA.debugLine="ToggleMenu";
_vvv2();
 //BA.debugLineNum = 188;BA.debugLine="End Sub";
return "";
}
public static String  _lblbackfromsettings_click() throws Exception{
 //BA.debugLineNum = 235;BA.debugLine="Private Sub lblBackFromSettings_Click";
 //BA.debugLineNum = 236;BA.debugLine="Log(\"+ lblBackFromSettings_Click +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834930689","+ lblBackFromSettings_Click +",0);
 //BA.debugLineNum = 237;BA.debugLine="Log(\"*   Removing views *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834930690","*   Removing views *",0);
 //BA.debugLineNum = 238;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 240;BA.debugLine="Log(\"*   Loading layout 'main' *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834930693","*   Loading layout 'main' *",0);
 //BA.debugLineNum = 241;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 243;BA.debugLine="Log(\"- lblBackFromSettings_Click -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834930696","- lblBackFromSettings_Click -",0);
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _lblexit_click() throws Exception{
 //BA.debugLineNum = 260;BA.debugLine="Private Sub lblExit_Click";
 //BA.debugLineNum = 261;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 262;BA.debugLine="End Sub";
return "";
}
public static String  _lblmenu_click() throws Exception{
 //BA.debugLineNum = 180;BA.debugLine="Private Sub lblMenu_Click";
 //BA.debugLineNum = 181;BA.debugLine="bMenuOpened = True";
_vv6 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 182;BA.debugLine="ToggleMenu";
_vvv2();
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _lblsettings_click() throws Exception{
int _m = 0;
int _s = 0;
 //BA.debugLineNum = 214;BA.debugLine="Private Sub lblSettings_Click";
 //BA.debugLineNum = 215;BA.debugLine="Log(\"+ lblSettings_Click +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799617","+ lblSettings_Click +",0);
 //BA.debugLineNum = 216;BA.debugLine="Log(\"*   Calling Stop *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799618","*   Calling Stop *",0);
 //BA.debugLineNum = 217;BA.debugLine="Stop";
_vvv3();
 //BA.debugLineNum = 218;BA.debugLine="Log(\"*   Back to lblSettings_Click *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799620","*   Back to lblSettings_Click *",0);
 //BA.debugLineNum = 219;BA.debugLine="Log(\"*   Removing views *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799621","*   Removing views *",0);
 //BA.debugLineNum = 220;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 222;BA.debugLine="Log(\"*   Loading layout 'settings' *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799624","*   Loading layout 'settings' *",0);
 //BA.debugLineNum = 223;BA.debugLine="Activity.LoadLayout(\"settings\")";
mostCurrent._activity.LoadLayout("settings",mostCurrent.activityBA);
 //BA.debugLineNum = 225;BA.debugLine="Log(\"*   Setting minutes and seconds text & slide";
anywheresoftware.b4a.keywords.Common.LogImpl("834799627","*   Setting minutes and seconds text & sliders *",0);
 //BA.debugLineNum = 226;BA.debugLine="Dim m As Int = GetMinutes";
_m = _vv4();
 //BA.debugLineNum = 227;BA.debugLine="Dim s As Int = GetSeconds";
_s = _vv5();
 //BA.debugLineNum = 228;BA.debugLine="lblMin.Text = strMinText & m";
mostCurrent._lblmin.setText(BA.ObjectToCharSequence(mostCurrent._vv7+BA.NumberToString(_m)));
 //BA.debugLineNum = 229;BA.debugLine="lblSec.Text = strSecText & s";
mostCurrent._lblsec.setText(BA.ObjectToCharSequence(mostCurrent._vv0+BA.NumberToString(_s)));
 //BA.debugLineNum = 230;BA.debugLine="seekBarMin.Value = m";
mostCurrent._seekbarmin.setValue(_m);
 //BA.debugLineNum = 231;BA.debugLine="seekBarSec.Value = s";
mostCurrent._seekbarsec.setValue(_s);
 //BA.debugLineNum = 232;BA.debugLine="Log(\"- lblSettings_Click -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834799634","- lblSettings_Click -",0);
 //BA.debugLineNum = 233;BA.debugLine="End Sub";
return "";
}
public static String  _lblstart_click() throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Private Sub lblStart_Click";
 //BA.debugLineNum = 169;BA.debugLine="bStarted = True";
_vv1 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 170;BA.debugLine="ToggleStartStopButtons";
_vv2();
 //BA.debugLineNum = 171;BA.debugLine="Start";
_vvv4();
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _lblstop_click() throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Private Sub lblStop_Click";
 //BA.debugLineNum = 175;BA.debugLine="bStarted = False";
_vv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 176;BA.debugLine="ToggleStartStopButtons";
_vv2();
 //BA.debugLineNum = 177;BA.debugLine="Stop";
_vvv3();
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

private static byte[][] bb;

public static String vvv13(final byte[] _b, final int i) throws Exception {
Runnable r = new Runnable() {
{

int value = i / 8 + 472655;
if (bb == null) {
		
                bb = new byte[4][];
				bb[0] = BA.packageName.getBytes("UTF8");
                bb[1] = BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionName.getBytes("UTF8");
                if (bb[1].length == 0)
                    bb[1] = "jsdkfh".getBytes("UTF8");
                bb[2] = new byte[] { (byte)BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionCode };			
        }
        bb[3] = new byte[] {
                    (byte) (value >>> 24),
						(byte) (value >>> 16),
						(byte) (value >>> 8),
						(byte) value};
				try {
					for (int __b = 0;__b < (3 + 1);__b ++) {
						for (int b = 0;b<_b.length;b++) {
							_b[b] ^= bb[__b][b % bb[__b].length];
						}
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
                

            
}
public void run() {
}
};
return new String(_b, "UTF8");
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Private xui As XUI";
_vvv1 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 22;BA.debugLine="Private tmrTimer As Timer";
_v7 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 24;BA.debugLine="Private nInterval As String = 30000 ' 30 s as def";
_v0 = BA.NumberToString(30000);
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _seekbarmin_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 252;BA.debugLine="Private Sub seekBarMin_ValueChanged (Value As Int,";
 //BA.debugLineNum = 253;BA.debugLine="UpdateInterval";
_vvv5();
 //BA.debugLineNum = 254;BA.debugLine="End Sub";
return "";
}
public static String  _seekbarsec_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 256;BA.debugLine="Private Sub seekBarSec_ValueChanged (Value As Int,";
 //BA.debugLineNum = 257;BA.debugLine="UpdateInterval";
_vvv5();
 //BA.debugLineNum = 258;BA.debugLine="End Sub";
return "";
}
public static String  _vvv4() throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Private Sub Start";
 //BA.debugLineNum = 130;BA.debugLine="Log(\"+ Start +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603009","+ Start +",0);
 //BA.debugLineNum = 131;BA.debugLine="Log(\"*   Setting interval *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603010","*   Setting interval *",0);
 //BA.debugLineNum = 132;BA.debugLine="tmrTimer.Interval = nInterval";
_v7.setInterval((long)(Double.parseDouble(_v0)));
 //BA.debugLineNum = 133;BA.debugLine="Log(\"*   Interval set to '\" & tmrTimer.Interval &";
anywheresoftware.b4a.keywords.Common.LogImpl("834603012","*   Interval set to '"+BA.NumberToString(_v7.getInterval())+"'",0);
 //BA.debugLineNum = 134;BA.debugLine="Log(\"*   Enabling timer *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603013","*   Enabling timer *",0);
 //BA.debugLineNum = 135;BA.debugLine="tmrTimer.Enabled = True";
_v7.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 136;BA.debugLine="Log(\"*   Timer enabled: '\" & tmrTimer.Enabled & \"";
anywheresoftware.b4a.keywords.Common.LogImpl("834603015","*   Timer enabled: '"+BA.ObjectToString(_v7.getEnabled())+"'",0);
 //BA.debugLineNum = 138;BA.debugLine="Log(\"*   Setting started *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603017","*   Setting started *",0);
 //BA.debugLineNum = 139;BA.debugLine="bStarted = True";
_vv1 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 140;BA.debugLine="Log(\"*   Started: '\" & bStarted & \"'\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603019","*   Started: '"+BA.ObjectToString(_vv1)+"'",0);
 //BA.debugLineNum = 141;BA.debugLine="ToggleStartStopButtons";
_vv2();
 //BA.debugLineNum = 142;BA.debugLine="Log(\"*   Calling Dong *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603021","*   Calling Dong *",0);
 //BA.debugLineNum = 143;BA.debugLine="Dong";
_vv3();
 //BA.debugLineNum = 144;BA.debugLine="Log(\"- Start -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("834603023","- Start -",0);
 //BA.debugLineNum = 145;BA.debugLine="End Sub";
return "";
}
public static String  _vvv3() throws Exception{
 //BA.debugLineNum = 151;BA.debugLine="Private Sub Stop";
 //BA.debugLineNum = 152;BA.debugLine="Log(\"+ Stop +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81245185","+ Stop +",0);
 //BA.debugLineNum = 153;BA.debugLine="Log(\"*   Disabling timer *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81245186","*   Disabling timer *",0);
 //BA.debugLineNum = 154;BA.debugLine="tmrTimer.Enabled = False";
_v7.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 155;BA.debugLine="Log(\"*   Timer enabled: '\" & tmrTimer.Enabled & \"";
anywheresoftware.b4a.keywords.Common.LogImpl("81245188","*   Timer enabled: '"+BA.ObjectToString(_v7.getEnabled())+"'",0);
 //BA.debugLineNum = 157;BA.debugLine="Log(\"*   Setting started *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81245190","*   Setting started *",0);
 //BA.debugLineNum = 158;BA.debugLine="bStarted = False";
_vv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 159;BA.debugLine="Log(\"*   Started: '\" & bStarted & \"'\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81245192","*   Started: '"+BA.ObjectToString(_vv1)+"'",0);
 //BA.debugLineNum = 160;BA.debugLine="ToggleStartStopButtons";
_vv2();
 //BA.debugLineNum = 161;BA.debugLine="Log(\"- Stop -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81245194","- Stop -",0);
 //BA.debugLineNum = 162;BA.debugLine="End Sub";
return "";
}
public static String  _ticktimer_tick() throws Exception{
 //BA.debugLineNum = 164;BA.debugLine="Private Sub TickTimer_Tick";
 //BA.debugLineNum = 165;BA.debugLine="Dong";
_vv3();
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _vvv2() throws Exception{
 //BA.debugLineNum = 109;BA.debugLine="Sub ToggleMenu";
 //BA.debugLineNum = 110;BA.debugLine="Log(\"+ ToggleMenu +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83407873","+ ToggleMenu +",0);
 //BA.debugLineNum = 111;BA.debugLine="If bMenuOpened = True Then";
if (_vv6==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 112;BA.debugLine="Log(\"+ Menu Opened +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83407875","+ Menu Opened +",0);
 //BA.debugLineNum = 113;BA.debugLine="pnlMenu.Enabled = True";
mostCurrent._pnlmenu.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="pnlMenu.Visible = True";
mostCurrent._pnlmenu.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="lblMenu.Enabled = False";
mostCurrent._lblmenu.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="lblMenu.Visible = False";
mostCurrent._lblmenu.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 119;BA.debugLine="Log(\"+ Menu Closed +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83407882","+ Menu Closed +",0);
 //BA.debugLineNum = 120;BA.debugLine="pnlMenu.Enabled = False";
mostCurrent._pnlmenu.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 121;BA.debugLine="pnlMenu.Visible = False";
mostCurrent._pnlmenu.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 123;BA.debugLine="lblMenu.Enabled = True";
mostCurrent._lblmenu.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 124;BA.debugLine="lblMenu.Visible = True";
mostCurrent._lblmenu.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 126;BA.debugLine="Log(\"- ToggleMenu -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83407889","- ToggleMenu -",0);
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _vv2() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub ToggleStartStopButtons";
 //BA.debugLineNum = 90;BA.debugLine="Log(\"+ ToggleStartStopButtons +\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83080193","+ ToggleStartStopButtons +",0);
 //BA.debugLineNum = 91;BA.debugLine="If bStarted = True Then";
if (_vv1==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 92;BA.debugLine="Log(\"*   Started *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83080195","*   Started *",0);
 //BA.debugLineNum = 93;BA.debugLine="lblStart.Enabled = False";
mostCurrent._lblstart.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 94;BA.debugLine="lblStart.Visible = False";
mostCurrent._lblstart.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="lblStop.Enabled = True";
mostCurrent._lblstop.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 97;BA.debugLine="lblStop.Visible = True";
mostCurrent._lblstop.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="Log(\"*   Stopped *\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83080202","*   Stopped *",0);
 //BA.debugLineNum = 100;BA.debugLine="lblStop.Enabled = False";
mostCurrent._lblstop.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 101;BA.debugLine="lblStop.Visible = False";
mostCurrent._lblstop.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 103;BA.debugLine="lblStart.Enabled = True";
mostCurrent._lblstart.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 104;BA.debugLine="lblStart.Visible = True";
mostCurrent._lblstart.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 106;BA.debugLine="Log(\"- ToggleStartStopButtons -\")";
anywheresoftware.b4a.keywords.Common.LogImpl("83080209","- ToggleStartStopButtons -",0);
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _vvv5() throws Exception{
 //BA.debugLineNum = 246;BA.debugLine="Private Sub UpdateInterval";
 //BA.debugLineNum = 247;BA.debugLine="nInterval = (seekBarMin.Value *   60 + seekBarSec";
_v0 = BA.NumberToString((mostCurrent._seekbarmin.getValue()*60+mostCurrent._seekbarsec.getValue())*1000);
 //BA.debugLineNum = 248;BA.debugLine="lblMin.Text = strMinText & seekBarMin.Value";
mostCurrent._lblmin.setText(BA.ObjectToCharSequence(mostCurrent._vv7+BA.NumberToString(mostCurrent._seekbarmin.getValue())));
 //BA.debugLineNum = 249;BA.debugLine="lblSec.Text = strSecText & seekBarSec.Value";
mostCurrent._lblsec.setText(BA.ObjectToCharSequence(mostCurrent._vv0+BA.NumberToString(mostCurrent._seekbarsec.getValue())));
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
}
