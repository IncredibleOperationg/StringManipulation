package osmedile.intellij.stringmanip.replace.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import osmedile.intellij.stringmanip.StringManipulationBundle;
import osmedile.intellij.stringmanip.config.PluginPersistentStateComponent;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryRenameDialog extends DialogWrapper {

    private final JPanel detailPanel;

    private final JPanel mainPanel = new JPanel(new BorderLayout());

    private final String historyName;

    private final JBTextField historyNameField = new JBTextField();


    public HistoryRenameDialog(Project project, JPanel detailPanel, ReplaceCompositeModel selectedValue) {
        super(project);
        this.detailPanel = detailPanel;
        this.historyName = selectedValue.getName();
        historyNameField.setText(historyName);

        setTitle(StringManipulationBundle.message("renameHistoryDialog.title"));
        mainPanel.setPreferredSize(JBUI.size(300, 30));
        JLabel historyNameLabel = new JBLabel(StringManipulationBundle.message("renameHistoryDialog.historyNameLabel"));

        mainPanel.add(historyNameLabel, BorderLayout.WEST);
        mainPanel.add(historyNameField);
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        List<ReplaceCompositeModel> replaceHistory = PluginPersistentStateComponent.getInstance().getReplaceHistory();
        ReplaceCompositeModel model = replaceHistory.stream().filter(replaceCompositeModel -> replaceCompositeModel.getName().equals(historyName)).findAny().get();
        model.setName(historyNameField.getText().trim());
        detailPanel.repaint();
        detailPanel.validate();
        super.doOKAction();
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        ArrayList<ValidationInfo> validationInfos = new ArrayList<>();
        if (StringUtils.isEmpty(historyNameField.getText())) {
            validationInfos.add(new ValidationInfo(StringManipulationBundle.message("deleteHistoryDialog.name.empty.error"), historyNameField));
        }
        if (StringUtils.equals(historyName, historyNameField.getText())) {
            validationInfos.add(new ValidationInfo(StringManipulationBundle.message("deleteHistoryDialog.name.same.error"), historyNameField));
        }
        return validationInfos;
    }
}
