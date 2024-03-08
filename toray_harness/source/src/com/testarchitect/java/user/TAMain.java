package com.testarchitect.java.user;
/**
Example "main" script for interpreting TestArchitect(tm) tests in Java.

A harness consists typically of a main module and one or more "script modules" that
host the functions to implement actions.
 
Recommended steps to create a new script module with actions:
- create a java script module
- add the following to the module:
    - a "setActions()" function that declares the actions to be implemented
    - a "divertAction" function that maps the actions to functions that implement them
    - a function for each action that the module needs to support, suggested name action_Something
- add an "import" statement here to import the module
- add a call in main() here to the module's "setActions" function.
- add a line in the divert() function here to direct actions to the new module

Disclaimer:
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/




import com.testarchitect.java.AbtAutomation;
import com.testarchitect.java.AbtLibrary;
import com.testarchitect.java.ITestRun;

public class TAMain implements ITestRun {
		
	/**
	 * the entry point
	 * @param args
	 */
	public static void main(String args[]) {
		

		//initialize the TestArchitect interpreter
		Lib_Interpret.init();

		//register actions (add your "setActions" functions here)
		Mod_Example.setActions();

		//crate an instance of TAMain
		TAMain mainRun = new TAMain();
		
		//interpret the test
		mainRun.runTest();
		
		//finish the test run
		Lib_Interpret.end();
		
		//exit process
		System.exit(0);
	}

	/**
	 * function to run test module
	 */
	private void runTest() {
		Lib_Interpret.interpret(this);
	}
	
	/**
	 *  divert the action to a script module
	 *  note: this function is called by the interpreter, it should return:
	 *   - True to tell the interpreter that the custom action has been consumed
	 *   - False if it cannot handle action from the module
	 */
	public boolean divert(String strModule, String strAction) {
		strModule = strModule.toLowerCase();

		if (strModule.equals("example")) {
			return Mod_Example.divert(strAction);
		} 
		else if (strModule.equals("eng_executenow();")) {
			return AbtLibrary.executeNow() != 0;
		} 
		else if (strModule.equals("abt automation")) {
			AbtAutomation.divert(strAction);
			return true;
		}
		else {
			AbtLibrary.handleUnknowAction();
			return false;
		}
	}
	
	
}
