/*
 * Copyright 2011 Ismail Marmoush
 * 
 * This file is part of JLongPath.
 * 
 * JLongPath is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License Version 3 as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * 
 * JLongPath is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * JLongPath. If not, see http://www.gnu.org/licenses/.
 * 
 * For More Information Please Visit http://marmoush.com
 */
package com.marmoush.JLongPath;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

// TODO: Auto-generated Javadoc
/**
 * The Class ShowDirectoryDialog.
 */
public class ShowDirectoryDialog
{

    /** The bar. */
    private ProgressBar bar;

    /** The bar maximum. */
    private final int barMaximum = 100;

    /** The browse src btn. */
    private Button browseSrcBtn;

    /** The button cancel. */
    private Button buttonCancel;

    /** The button dest. */
    private Button buttonDest;

    /** The copier. */
    final private Copier copier = new Copier();

    /** The copier thread busy. */
    private boolean copierThreadBusy;

    /** The copy. */
    private Button copy;

    /** The display. */
    private final Display display;

    /** The label status. */
    private Label labelStatus;

    /** The shell. */
    private final Shell shell;

    /** The text box dest. */
    private Text textBoxDest;

    /** The text box src. */
    private Text textBoxSrc;

    /**
     * Instantiates a new show directory dialog.
     */
    public ShowDirectoryDialog()
    {
	display = new Display();
	shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
	shell.setLayout(new GridLayout(6, false));
	shell.setText("JLong Path Copier");

	// Source
	Label srcLabel = new Label(shell, SWT.NONE);
	srcLabel.setText("Source:");

	this.textBoxSrc = new Text(shell, SWT.BORDER);
	this.browseSrcBtn = new Button(shell, SWT.PUSH);

	// Destination
	Label destLabel = new Label(shell, SWT.NONE);
	destLabel.setText("Destination:");

	this.textBoxDest = new Text(shell, SWT.BORDER);
	this.buttonDest = new Button(shell, SWT.PUSH);

	// Copying Button
	this.copy = new Button(shell, SWT.PUSH);
	this.buttonCancel = new Button(shell, SWT.PUSH);
	// progress bar
	this.bar = new ProgressBar(shell, SWT.SMOOTH);
	this.bar.setMaximum(this.barMaximum);

	// status label
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 4;
	this.labelStatus = new Label(shell, SWT.NONE);
	this.labelStatus.setText("Status: Ready!");
	this.labelStatus.setLayoutData(data);
	// widgets
	setSrcWidgets(shell, this.textBoxSrc);
	setDestWidgets(shell, this.textBoxDest);
	setCopierButton(shell, display, bar, this.copy, this.labelStatus);
	setCancelButton(this.buttonCancel);
	// Rest
	shell.pack();
	shell.open();
	while (!shell.isDisposed())
	{
	    if (!display.readAndDispatch())
	    {
		display.sleep();
	    }
	}
    }

    /**
     * Sets the cancel button.
     * 
     * @param cancelButton the new cancel button
     */
    private void setCancelButton(final Button cancelButton)
    {
	cancelButton.setText("Cancel");
	cancelButton.addSelectionListener(new SelectionAdapter()
	{
	    @Override
	    public void widgetSelected(SelectionEvent arg0)
	    {
		if (isCopierThreadBusy())
		{
		    copier.setKeepCopying(false);
		}
	    }
	});
    }

    /**
     * Sets the copier button.
     * 
     * @param shell the shell
     * @param display the display
     * @param bar the bar
     * @param copyBtn the copy btn
     * @param statusLabel the status label
     */
    private void setCopierButton(final Shell shell, final Display display,
	final ProgressBar bar, final Button copyBtn, final Label statusLabel)
    {
	copyBtn.setText("Copy ");
	copyBtn.addSelectionListener(new SelectionAdapter()
	{
	    @Override
	    public void widgetSelected(SelectionEvent event)
	    {
		final String src = textBoxSrc.getText();
		final String dest = textBoxDest.getText();
		startCopyingThread(src, dest);
		startCopyingWatchThread(src);
	    }
	});
    }

    /**
     * Checks if is copier thread busy.
     * 
     * @return true, if is copier thread busy
     */
    private boolean isCopierThreadBusy()
    {
	return copierThreadBusy;
    }

    /**
     * Reset.
     */
    private void reset()
    {
	this.copier.setKeepCopying(true);
	setCopierThreadBusy(false);
	display.asyncExec(new Runnable()
	{
	    public void run()
	    {
		if (bar.isDisposed())
		    return;
		bar.setSelection(0);
		labelStatus.setText("Ready!");
		textBoxSrc.setText("");
		textBoxDest.setText("");
	    }
	});
    }

    /**
     * Sets the copier thread busy.
     * 
     * @param copierThreadBusy the new copier thread busy
     */
    private void setCopierThreadBusy(boolean copierThreadBusy)
    {
	this.copierThreadBusy = copierThreadBusy;
    }

    /**
     * Sets the dest widgets.
     * 
     * @param shell the shell
     * @param textDest the text dest
     */
    private void setDestWidgets(final Shell shell, final Text textDest)
    {
	// Create the text box extra wide to show long paths
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 4;
	textDest.setLayoutData(data);
	textDest.setEditable(false);
	// Clicking the button will allow the user
	// to select a directory
	buttonDest.setText("Browse...");
	buttonDest.addSelectionListener(new SelectionAdapter()
	{
	    @Override
	    public void widgetSelected(SelectionEvent event)
	    {
		DirectoryDialog dlg = new DirectoryDialog(shell);

		// Set the initial filter path according
		// to anything they've selected or typed in
		dlg.setFilterPath(textDest.getText());

		// Change the title bar text
		dlg.setText("SWT's DirectoryDialog");

		// Customizable message displayed in the dialog
		dlg.setMessage("Select a directory");

		// Calling open() will open and run the dialog.
		// It will return the selected directory, or
		// null if user cancels
		String dir = dlg.open();
		if (dir != null)
		{
		    // Set the text box to the new selection
		    textDest.setText(dir);
		}
	    }
	});
    }

    /**
     * Sets the src widgets.
     * 
     * @param shell the shell
     * @param text the text
     */
    private void setSrcWidgets(final Shell shell, final Text text)
    {

	// Create the text box extra wide to show long paths
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 4;
	text.setLayoutData(data);
	text.setEditable(false);
	// Clicking the button will allow the user
	// to select a directory
	browseSrcBtn.setText("Browse...");
	browseSrcBtn.addSelectionListener(new SelectionAdapter()
	{
	    @Override
	    public void widgetSelected(SelectionEvent event)
	    {
		DirectoryDialog dlg = new DirectoryDialog(shell);
		// Set the initial filter path according
		// to anything they've selected or typed in
		dlg.setFilterPath(text.getText());

		// Change the title bar text
		dlg.setText("SWT's DirectoryDialog");

		// Customizable message displayed in the dialog
		dlg.setMessage("Select a directory");

		// Calling open() will open and run the dialog.
		// It will return the selected directory, or
		// null if user cancels
		String dir = dlg.open();
		if (dir != null)
		{
		    // Set the text box to the new selection
		    text.setText(dir);
		}
	    }
	});
    }

    /**
     * Start copying thread.
     * 
     * @param src the src
     * @param dest the dest
     */
    private void startCopyingThread(final String src, final String dest)
    {
	new Thread()
	{
	    public void run()
	    {
		// start
		setCopierThreadBusy(true);
		copier.setKeepCopying(true);
		try
		{
		    copier.copy(src, dest);
		    reset();
		} catch (final InvalidPathException e)
		{
		    reset();
		    display.asyncExec(new Runnable()
		    {
			public void run()
			{
			    MessageBox mb = new MessageBox(shell, SWT.ERROR);
			    mb.setMessage("Please Select a valid path");
			    mb.open();
			}
		    });

		} catch (final IOException e)
		{
		    reset();
		    display.asyncExec(new Runnable()
		    {
			public void run()
			{
			    MessageBox mb = new MessageBox(shell, SWT.ERROR);
			    String[] ex = e.getClass().getName().split("[.]");
			    mb.setMessage(ex[ex.length - 1] + " \" "
				    + e.getMessage() + " \".");
			    mb.open();
			}
		    });
		}
		if (display.isDisposed())
		    return;
	    }
	}.start();
    }

    /**
     * Start copying watch thread.
     * 
     * @param src the src
     */
    private void startCopyingWatchThread(final String src)
    {
	new Thread()
	{
	    public void run()
	    {

		try
		{
		    final long nFilesTotal = copier.countFiles(src);
		    long copiedFiles = copier.getnFilesCopied();
		    // TODO make it While loop
		    if (isCopierThreadBusy() && copiedFiles == 0)
		    {
			Thread.sleep(20);
			copiedFiles = copier.getnFilesCopied();
		    }
		    while (isCopierThreadBusy())
		    {
			display.asyncExec(new Runnable()
			{
			    public void run()
			    {
				if (bar.isDisposed())
				    return;
				bar.setSelection(copier.getPercentage(nFilesTotal));
				labelStatus.setText("Copying: "
					+ copier.getFileBeingCopied());
			    }
			});
			copiedFiles = copier.getnFilesCopied();
			Thread.sleep(25);
		    }
		    reset();
		} catch (final InterruptedException e)
		{
		    display.asyncExec(new Runnable()
		    {
			public void run()
			{
			    MessageBox mb = new MessageBox(shell, SWT.ERROR);
			    mb.setMessage(e.getMessage());
			    mb.open();
			}
		    });
		} catch (final IOException e)
		{
		    display.asyncExec(new Runnable()
		    {
			public void run()
			{
			    MessageBox mb = new MessageBox(shell, SWT.ERROR);
			    mb.setMessage(e.getMessage());
			    mb.open();
			}
		    });
		}
		// RESET cancel locks
		if (display.isDisposed())
		    return;
	    }
	}.start();
    }
}