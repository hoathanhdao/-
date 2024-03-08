package com.testarchitect.java.user;


import com.testarchitect.java.AbtAutomation;
import com.testarchitect.java.AbtLibrary;
import com.testarchitect.java.ITestRun;
import com.testarchitect.java.TAConstants;

public class Lib_Interpret {
	
	/**
	 * Initialize automation 
	 */
	public static void init(){
		
		// Starts engine
		AbtLibrary.start();

		// Automation object must be created after engine has run
		AbtAutomation.create();
	}

	/**
	 * Loop through the actions and execute; 
	 * @param run
	 * @return
	 */
	public static boolean interpret(ITestRun run){
		boolean runningFlag = true;
		int actionType = 0;
		String actionName = "";
		String module = "";

		try{

			/* -------------START EXECUTION-------------*/
			while ( runningFlag ) 
			{
				actionType = AbtLibrary.nextAction();

				if ( actionType == TAConstants.NONEXTACTION || actionType == TAConstants.NEXTACTIONFAILURE ) {
					/* -------------END EXECUTION-------------*/
					runningFlag = false;
					break;
				}
				AbtLibrary.getNextActionStatus();
				if ( actionType == TAConstants.SKIPPING ) {
					//pass;

				} else if ( actionType == TAConstants.ENDOFSUBTEST ) {
					AbtLibrary.returnFromSubTest();

				} else if ( actionType == TAConstants.ENDOFACTIONDEFINITION ) {
					AbtLibrary.returnFromDefinedAction();

				} else if ( actionType == TAConstants.UNKNOWN ) {
					AbtLibrary.handleUnknowAction();

				} else {
					actionName = AbtLibrary.getArgByIndex(0);
					module = AbtLibrary.getActionScript();


					if ( actionType == TAConstants.HEADER || actionType == TAConstants.BUILTIN || actionType == TAConstants.USER ) {
						if(!module.equals("")){
							divert(run, module, actionName);
						}
					} else if ( actionType == TAConstants.ACTIONDEFINITION ) {
						AbtLibrary.divertToDefinedAction(module);
					}
				}
			}
		} catch(Exception e) {
			return false;

		}
		return true;
	}

	/**
	 * Finish task, do the cleanup and report 
	 */
	public static void end(){
		AbtAutomation.endRun();
		AbtLibrary.endAbt();
		AbtAutomation.destroy();
	}

	/**
	 * Divert to proper module and action 
	 * @param run
	 * @param module
	 * @param actionName
	 */
	public static void divert(ITestRun run, String module, String actionName){
		if ( cleanComp(module, "eng_ExecuteNow();") ) {
			AbtLibrary.executeNow();
		} else if ( cleanComp(module, TAConstants.MODULE_TO_DEPUTY) ) {
			AbtLibrary.divertToDeputy();
		} else if ( cleanComp(module, TAConstants.MODULE_AUTOMATION) ) {
			AbtAutomation.divert(actionName);
		} else if ( run.divert(module,actionName) ) {

		} else {
			AbtLibrary.handleUnknowAction();
		}
	}

	/**
	 * work around for a problem with JNI 
	 * @param s1
	 * @param s2
	 * @return
	 */
	static private boolean cleanComp(String s1, String s2) {
		if ( s1.length() != s2.length() ) return false;
		for (int i=0; i<s1.length(); i++) {
			int a = (int) s1.charAt(i);
			int b = (int) s2.charAt(i);
			if ( a != b )
				return false;
		}
		return true;
	}
}
