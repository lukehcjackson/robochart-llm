package circus.robocalc.robochart.generator.llm;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StyledText;




public class MultiLineInputDialog extends Dialog {

    private String title;
    private String message;
    private String inputText = "";

    private StyledText textArea;

    public MultiLineInputDialog(Shell parentShell, String title, String message) {
        super(parentShell);
        this.title = title;
        this.message = message;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        if (message != null) {
            Label label = new Label(container, SWT.NONE);
            label.setText(message);
        }

        textArea = new StyledText(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        gd.widthHint = 400;
        textArea.setLayoutData(gd);

        return container;
    }

    @Override
    protected void okPressed() {
        inputText = textArea.getText();
        super.okPressed();
    }

    public String getInputText() {
        return inputText;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }
}

