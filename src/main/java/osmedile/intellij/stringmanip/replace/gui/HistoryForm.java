package osmedile.intellij.stringmanip.replace.gui;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.SimpleListCellRenderer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import osmedile.intellij.stringmanip.align.ArrayListModel;
import osmedile.intellij.stringmanip.config.PluginPersistentStateComponent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import static osmedile.intellij.stringmanip.StringManipulationBundle.message;

import static java.util.function.Predicate.not;

public class HistoryForm {
	private JList<ReplaceCompositeModel> list;
	private JPanel detail;
	public JPanel root;
	private CompositeForm compositeForm;

	public HistoryForm() {
		List<ReplaceCompositeModel> history = new ArrayListModel<>(PluginPersistentStateComponent.getInstance().getReplaceHistory());
		history.removeIf(not(ReplaceCompositeModel::isValid));
		history.sort(Comparator.comparing(ReplaceCompositeModel::getName).reversed());
		ArrayListModel<ReplaceCompositeModel> model = new ArrayListModel<>(history);
		list.setModel(model);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				compositeForm.initModel(list.getSelectedValue());
				detail.revalidate();
				detail.repaint();
			}
		});
		;
		list.setCellRenderer(new SimpleListCellRenderer<ReplaceCompositeModel>() {
			@Override
			public void customize(@NotNull JList<? extends ReplaceCompositeModel> jList, ReplaceCompositeModel replaceCompositeModel, int i, boolean b, boolean b1) {
				if (StringUtils.isNotBlank(replaceCompositeModel.name)) {
					setText(replaceCompositeModel.name);
				} else {
					setText("---");
				}

			}
		});
		if (!history.isEmpty()) {
			list.setSelectedIndex(0);
			list.revalidate();
			list.repaint();
		}
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e) && !e.isConsumed()) {
					createPopupMenu().show(list, e.getX(), e.getY());
					e.consume();
				}
			}
		});
	}

	private JBPopupMenu createPopupMenu() {
		JBPopupMenu popupMenu = new JBPopupMenu();

		JBMenuItem renameMenuItem = new JBMenuItem(message("renameHistory.menuItem.name"));
		ReplaceCompositeModel selectedValue = list.getSelectedValue();
		renameMenuItem.addActionListener(e -> new HistoryRenameDialog(null, root, selectedValue).show());
		popupMenu.add(renameMenuItem);

		JBMenuItem deleteMenuItem = new JBMenuItem(message("deleteHistory.menuItem.name"));
		deleteMenuItem.addActionListener(e -> {
			int result = Messages.showOkCancelDialog(
					message("deleteHistoryDialog.message"),
					message("deleteHistoryDialog.title"),
					message("deleteHistoryDialog.ok"),
					message("deleteHistoryDialog.cancel"), Messages.getQuestionIcon());
			if (result != Messages.OK) {
				return;
			}

			List<ReplaceCompositeModel> replaceCompositeModelList = PluginPersistentStateComponent.getInstance().getReplaceHistory();
            replaceCompositeModelList.removeIf(replaceCompositeModel -> replaceCompositeModel.equals(selectedValue));

			refreshComponent(replaceCompositeModelList);
		});
		popupMenu.add(deleteMenuItem);

		JBMenuItem deleteOtherMenuItem = new JBMenuItem(message("deleteOtherHistory.menuItem.name"));
		deleteOtherMenuItem.addActionListener(e -> {
			int result = Messages.showOkCancelDialog(
					message("deleteOtherHistoryDialog.message"),
					message("deleteOtherHistoryDialog.title"),
					message("deleteOtherHistoryDialog.ok"),
					message("deleteOtherHistoryDialog.cancel"), Messages.getQuestionIcon());
			if (result != Messages.OK) {
				return;
			}
			List<ReplaceCompositeModel> replaceCompositeModelList = PluginPersistentStateComponent.getInstance().getReplaceHistory();
			replaceCompositeModelList.removeIf(replaceCompositeModel -> !replaceCompositeModel.getName().equals(selectedValue.getName()));

			refreshComponent(replaceCompositeModelList);
		});
		popupMenu.add(deleteOtherMenuItem);

		JBMenuItem deleteAllMenuItem = new JBMenuItem(message("deleteAllHistory.menuItem.name"));
		deleteAllMenuItem.addActionListener(e -> {
			int result = Messages.showOkCancelDialog(
					message("deleteAllHistoryDialog.message"),
					message("deleteAllHistoryDialog.title"),
					message("deleteAllHistoryDialog.ok"),
					message("deleteAllHistoryDialog.cancel"), Messages.getQuestionIcon());
			if (result != Messages.OK) {
				return;
			}
			List<ReplaceCompositeModel> replaceCompositeModelList = null;

			detail.remove(0);
			PluginPersistentStateComponent.getInstance().getReplaceHistory().clear();
			list.setModel(new ArrayListModel<>());
			root.revalidate();
			root.repaint();
		});
		popupMenu.add(deleteAllMenuItem);

		return popupMenu;
	}

	private void refreshComponent(List<ReplaceCompositeModel> replaceCompositeModelList) {
		list.setModel(new ArrayListModel<>(replaceCompositeModelList));
		if (replaceCompositeModelList.isEmpty()) {
			detail.remove(0);
		} else {
			list.getSelectionModel().addSelectionInterval(0, 0);
		}
		PluginPersistentStateComponent.getInstance().setReplaceHistory(replaceCompositeModelList);
		root.revalidate();
		root.repaint();
	}

	private void createUIComponents() {
		compositeForm = new CompositeForm(null, true);
		detail = compositeForm.expressions;
	}

	public ReplaceCompositeModel getModel() {
		return compositeForm.getModel();
	}
}
