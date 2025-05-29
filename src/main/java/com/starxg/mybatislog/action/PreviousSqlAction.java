package com.starxg.mybatislog.action;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.ex.MarkupIterator;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import org.jetbrains.annotations.NotNull;

/**
 * PreviousSqlAction
 * @author huangxingguang
 */
public class PreviousSqlAction extends JumpSqlAction {

    public PreviousSqlAction(ConsoleViewImpl consoleView) {
        super("Previous SQL", "Previous SQL", AllIcons.Actions.PreviousOccurence, consoleView);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        super.actionPerformed(e);

        final int offset = editor.getCaretModel().getPrimaryCaret().getOffset();
        if (offset <= 1) {
            return;
        }

        final int movedOffset = jump(0, offset - 1, false);

        if (movedOffset > -1) {
            if (e.getInputEvent().isShiftDown()) {
                editor.getSelectionModel().setSelection(offset, movedOffset);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(hasPrev());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // 根据操作性质二选一：
    
        // 选项1：UI操作（推荐）
        return ActionUpdateThread.EDT;  // 用于界面更新、按钮状态变更等
    
        // 选项2：后台操作
        // return ActionUpdateThread.BGT;  // 用于计算密集型或耗时操作
    }    
    @Override
    protected boolean isValid(RangeHighlighterEx next, int startOffset, int endOffset) {
        return super.isValid(next, startOffset, endOffset) && editor.getDocument().getLineNumber(endOffset) != editor.getDocument().getLineNumber(next.getStartOffset());
    }

    private boolean hasPrev() {

        final int offset = editor.getCaretModel().getPrimaryCaret().getOffset();
        if (offset <= 1) {
            return false;
        }

        final MarkupModelEx model = (MarkupModelEx) editor.getMarkupModel();
        final MarkupIterator<RangeHighlighterEx> iterator = model.overlappingIterator(0, offset - 1);
        try {
            return iterator.hasNext() && isValid(iterator.next(), 0, offset - 1);
        } finally {
            iterator.dispose();
        }

    }
}
