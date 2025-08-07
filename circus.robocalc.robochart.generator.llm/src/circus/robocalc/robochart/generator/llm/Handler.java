package circus.robocalc.robochart.generator.llm;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.core.resources.IFile;
import java.net.URI;
import org.eclipse.ui.PlatformUI;

public class Handler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		var selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection instanceof TreeSelection) {
			try {
				
				Boolean inputChoice = readChoice("RoboChart LLM Generator", "Generate a new model, or edit the selected model?", "Generate a new model", "Edit selected model");
				
				if (inputChoice != null) {
					
					/*
					 * user selects first option - generate a new model
					 */
					if (inputChoice) {
						
						String inputValue = readMultiLineInput("RoboChart LLM Generator", "Enter the requirements of your robotic system. Read more guidance ...here...");
						
						String[] args = {inputValue};
						LLMgen.LLMgenMain(args);
						
					
					/*
					 * user selects second option - edit the current model
					 */
					} else {
						
						//get the element selected, check if its a .rct or .rst file
						//if it is, display the prompting box and run some method
						
						Object firstElement = ((StructuredSelection) selection).getFirstElement();

					    if (firstElement instanceof IFile) {
					        IFile file = (IFile) firstElement;

					        // get the URI of the selected file
					        URI uriResource = file.getLocationURI();

					        // extract the file extension
					        String uriString = uriResource.toString();
					        String[] parts = uriString.split("\\.");
					        String ext = parts[parts.length - 1];

					        if ("rct".equals(ext) || "rst".equals(ext)) {
					            
								String inputValue = readMultiLineInput("RoboChart LLM Generator", "Enter your prompt to modify the selected file. Read more guidance ...here...");

								//pass the prompt and the contents of the selected file to the LLM
								
								//args can have the input prompt and the file
								//in the main code, check args to see what we are doing with the LLM
								//probably want to have some sort of memory - store the prompts and responses from the LLM, so in edit mode the LLM has memory of what it has already done
					            
					        } else {
					        	//display an error message popup - the file selected is not a .rct or .rst, so cannot be edited with this tool
					        	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				                MessageDialog.openError(shell, "Invalid File Type", "Please select an '.rct' or '.rst' file.");
					        }
					        
					    } else {
					    	//user has selected something that isn't a file
					    	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				            MessageDialog.openError(shell, "Invalid Selection", "Please select an '.rct' or '.rst' file.");
					    	
					    }
						
					}
				
				} else {
					//user has unexpectedly closed the dialog box
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
		return null;
	}
	
	public String readInput(String title, String message, IInputValidator validator) {
		var shell = Display.getCurrent().getActiveShell();
		System.out.println(shell);
		var dlg = new InputDialog(shell, title, message, "", validator);
		System.out.println(dlg);
		if (dlg.open() == Window.OK) {
			return dlg.getValue();
		}
		return null;
	}
	
	public String readMultiLineInput(String title, String message) {
	    Shell shell = Display.getCurrent().getActiveShell();
	    if (shell == null) {
	        shell = new Shell(Display.getDefault());
	    }

	    MultiLineInputDialog dialog = new MultiLineInputDialog(shell, title, message);
	    if (dialog.open() == Window.OK) {
	        return dialog.getInputText();
	    }
	    return null;
	}
	
	public Boolean readChoice(String title, String message, String option1, String option2) {
	    Shell shell = Display.getCurrent().getActiveShell();
	    if (shell == null) {
	        shell = new Shell(Display.getDefault());
	    }

	    String[] buttonLabels = { option1, option2 };
	    MessageDialog dialog = new MessageDialog(
	        shell,
	        title,
	        null,   // No image
	        message,
	        MessageDialog.QUESTION,  // Icon type
	        buttonLabels,
	        0  // Default selection index (option1)
	    );

	    int result = dialog.open();
	    if (result == 0) {
	        return true;
	    } else if (result == 1) {
	        return false;
	    }
	    return null;  // Shouldn't reach here unless dialog is closed unexpectedly
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
