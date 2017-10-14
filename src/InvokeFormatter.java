import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;

/**
 * Created by @author @pillravi on 10/13/17.
 */
public class InvokeFormatter extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        /* Obtain all the IntelliJ IDEA components we need, including the
         * project and editor */
        final Project project = event.getProject();
        if (project == null) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project)
                .getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        final Document document = editor.getDocument();

        FileDocumentManager fileDocMgr = FileDocumentManager.getInstance();
        /* Save the file to the disk so that when we read from the virtual file
         * (on the disk), it is the same as what the user sees in the editor */
        fileDocMgr.saveDocument(document);
        VirtualFile virtualFile = fileDocMgr.getFile(document);
        if (virtualFile == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                /* Rebuild/reparse the PSI so we can format it to be pretty */
                PsiDocumentManager documentManager =
                        PsiDocumentManager.getInstance(project);
                documentManager.commitDocument(document);

                /* Format the code so that it will be pretty */
                CodeStyleManager styleManager =
                        CodeStyleManager.getInstance(project);
                PsiElement psiFile = event.getData(LangDataKeys.PSI_FILE);
                styleManager.reformat(psiFile);
            }
        });
    }
}
