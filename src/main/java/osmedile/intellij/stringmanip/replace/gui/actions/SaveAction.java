package osmedile.intellij.stringmanip.replace.gui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import osmedile.intellij.stringmanip.StringManipulationBundle;
import osmedile.intellij.stringmanip.config.PluginPersistentStateComponent;
import osmedile.intellij.stringmanip.replace.gui.CompositeForm;
import osmedile.intellij.stringmanip.replace.gui.ReplaceCompositeModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static osmedile.intellij.stringmanip.StringManipulationBundle.message;

public class SaveAction extends DumbAwareAction {

	private CompositeForm form;
	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		form = CompositeForm.PANEL.getData(e.getDataContext());
		if (form != null) {
			new HistorySetNameDialog(e.getProject()).show();
		}
	}

	private class HistorySetNameDialog extends DialogWrapper {

		private final JPanel mainPanel = new JPanel(new BorderLayout());
		private final JBTextField historyNameField = new JBTextField();
		protected HistorySetNameDialog(@Nullable Project project) {
			super(project);
			setTitle(StringManipulationBundle.message("setNameHistoryDialog.title"));

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
			ReplaceCompositeModel replaceCompositeModel = form.getModel();
			replaceCompositeModel.setName(historyNameField.getText().trim());
			form.addToHistory();
			super.doOKAction();
		}

		@Override
		protected @Nullable ValidationInfo doValidate() {
			if (StringUtils.isBlank(historyNameField.getText())) {
				return new ValidationInfo(message("setNameHistoryDialog.name.empty.error"), historyNameField);
			}
			List<ReplaceCompositeModel> replaceCompositeModelList = PluginPersistentStateComponent.getInstance().getReplaceHistory();
			if (CollectionUtils.isNotEmpty(replaceCompositeModelList)) {
				Optional<ReplaceCompositeModel> optional = replaceCompositeModelList.stream().filter(replaceCompositeModel -> replaceCompositeModel.getName().equals(historyNameField.getText().trim())).findAny();
				if (optional.isPresent()) {
					return new ValidationInfo(message("setNameHistoryDialog.name.exist.error"), historyNameField);
				}
			}
			return super.doValidate();
		}
	}
}
