/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.locator.jface;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Wizard} error messages. Typically this is used with the
 * {@link IUIContext#assertThat(com.windowtester.runtime.condition.ICondition)} method to
 * assert that an error message is displayed or not.
 * <pre>
 * ui.assertThat(new WizardErrorMessageLocator().hasText(&quot;Some message here&quot;));
 * </pre>
 */
public class WizardErrorMessageLocator
	implements HasText
{
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.HasText#getText(com.windowtester.runtime.IUIContext)
	 */
	public String getText(IUIContext ui) throws WidgetSearchException {
		final String[] errorMessage = new String[1];
		final Exception[] exception = new Exception[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell activeShell = Display.getDefault().getActiveShell();
				if (activeShell == null) {
					exception[0] = new WidgetSearchException("No active shell");
					return;
				}
				Object dialog = activeShell.getData();
				if (!(dialog instanceof WizardDialog)) {
					exception[0] = new WidgetSearchException("Expected WizardDialog but found " + dialog);
					return;
				}
				final IWizardPage page = ((WizardDialog) dialog).getCurrentPage();
				if (page == null) {
					exception[0] = new WidgetSearchException("WizardDialog current page is null");
					return;
				}
				errorMessage[0] = page.getErrorMessage();
			}
		});
		if (exception[0] != null)
			throw ((WidgetSearchException) exception[0]);
		return errorMessage [0];
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
}
