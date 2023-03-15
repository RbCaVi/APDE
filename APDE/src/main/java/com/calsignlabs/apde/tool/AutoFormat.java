package com.calsignlabs.apde.tool;

import android.view.MenuItem;

import com.calsignlabs.apde.APDE;
import com.calsignlabs.apde.CodeEditText;
import com.calsignlabs.apde.KeyBinding;
import com.calsignlabs.apde.R;

/**
 * Runs the code through Processing's Auto Formatter
 */
public class AutoFormat implements Tool {
	public static final String PACKAGE_NAME = "com.calsignlabs.apde.tool.AutoFormat";
	
	private APDE context;
	
	@Override
	public void init(APDE context) {
		this.context = context;
	}
	
	@Override
	public String getMenuTitle() {
		return context.getResources().getString(R.string.tool_auto_format);
	}

        private static final Pattern INDENT_REGEX = Pattern.compile("^(\\s*)");
	
	@Override
	public void run() {
		CodeEditText code = context.getEditor().getSelectedCodeArea();
		
		if(!context.isExample() && code != null) {
			processing.app.Preferences.setInteger("editor.tabs.size", 2);
			
			if (getSelectionStart() == getSelectionEnd() ) {
			    code.setUpdateText((new processing.mode.java.AutoFormat()).format(code.getText().toString()));
                        } else {
                            
			Matcher matcher=INDENT_REGEX.matcher(line);
			
			String codeText = code.getText().toString();
			
			boolean trailingNewline = codeText.charAt(codeText.length() - 1) == '\n';
			
			String[] lines = codeText.split("\n");
			
			int startLine = code.lineForOffset(code.getSelectionStart());
			int endLine = code.lineForOffset(code.getSelectionEnd()) + 1;
			
			String[] toIndent = new String[endLine - startLine];
			System.arraycopy(lines, startLine, toIndent, 0, endLine - startLine);
	
			for(int i = 0; i < toIndent.length; i ++) {
				toIndent[i] = indent + toIndent[i];
			}
			
			System.arraycopy(toIndent, 0, lines, startLine, endLine - startLine);
			
			String text = "";
			for(String line : lines) {
				text += line + "\n";
			}
			
			if(!trailingNewline) {
				text = text.substring(0, text.length() - 1);
			}
			
			code.setUpdateText(text);
			code.clearTokens();
			
			code.setSelection(code.offsetForLine(startLine), code.offsetForLineEnd(endLine - 1) - (trailingNewline || endLine < lines.length ? 1 : 0));
			//The current implementation of this function is ugly, but we don't have any alternatives...
			code.startSelectionActionMode();
                        }

			code.clearTokens();
			code.flagRefreshTokens();
			
			context.getEditor().message(context.getResources().getString(R.string.tool_auto_format_success));
		}
	}
	
	@Override
	public KeyBinding getKeyBinding() {
		return context.getEditor().getKeyBindings().get("auto_format");
	}
	
	@Override
	public boolean showInToolsMenu(APDE.SketchLocation sketchLocation) {
		return !sketchLocation.isExample();
	}
	
	@Override
	public boolean createSelectionActionModeMenuItem(MenuItem convert) {
		//TODO maybe support auto formatting of selection, not just the entire file
		return false;
	}
}
