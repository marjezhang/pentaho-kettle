 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

 
/*
 * Created on 18-mei-2003
 *
 */

package be.ibridge.kettle.trans.step.accessoutput;

import java.io.File;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.util.StringUtil;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.step.BaseStepDialog;
import be.ibridge.kettle.trans.step.BaseStepMeta;
import be.ibridge.kettle.trans.step.StepDialogInterface;
import be.ibridge.kettle.trans.step.textfileinput.VariableButtonListenerFactory;

import com.healthmarketscience.jackcess.Database;


public class AccessOutputDialog extends BaseStepDialog implements StepDialogInterface
{
    private Label        wlFilename;
    private Button       wbbFilename; // Browse: add file or directory
    private Button       wbvFilename; // Variable
    private Text         wFilename;
    private FormData     fdlFilename, fdbFilename, fdbvFilename, fdFilename;

    private Label        wlCreateFile;
    private Button       wCreateFile;
    private FormData     fdlCreateFile, fdCreateFile;
    
    private Label        wlTablename;
    private Text         wTablename;
    private Button       wbbTablename, wbvTablename;
    private FormData     fdlTablename, fdTablename, fdbTablename, fdvTablename;

    /*
	private Label        wlTruncate;
	private Button       wTruncate;
	private FormData     fdlTruncate, fdTruncate;
    */
    
    private Label        wlCreateTable;
    private Button       wCreateTable;
    private FormData     fdlCreateTable, fdCreateTable;


    private AccessOutputMeta input;
	
	public AccessOutputDialog(Shell parent, Object in, TransMeta transMeta, String sname)
	{
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input=(AccessOutputMeta)in;
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);

		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
        SelectionAdapter lsSelMod = new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                input.setChanged();
            }
        };
		backupChanged = input.hasChanged();
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("AccessOutputDialog.DialogTitle"));
		
		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("System.Label.StepName"));
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, 0);
		fdlStepname.top  = new FormAttachment(0, 0);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, margin);
		fdStepname.top  = new FormAttachment(0, 0);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

        // Filename line
        wlFilename=new Label(shell, SWT.RIGHT);
        wlFilename.setText(Messages.getString("AccessOutputDialog.Filename.Label"));
        props.setLook(wlFilename);
        fdlFilename=new FormData();
        fdlFilename.left = new FormAttachment(0, 0);
        fdlFilename.top  = new FormAttachment(wStepname, margin);
        fdlFilename.right= new FormAttachment(middle, 0);
        wlFilename.setLayoutData(fdlFilename);

        wbbFilename=new Button(shell, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbFilename);
        wbbFilename.setText(Messages.getString("System.Button.Browse"));
        wbbFilename.setToolTipText(Messages.getString("System.Tooltip.BrowseForFileOrDirAndAdd"));
        fdbFilename=new FormData();
        fdbFilename.right= new FormAttachment(100, 0);
        fdbFilename.top  = new FormAttachment(wStepname, margin);
        wbbFilename.setLayoutData(fdbFilename);

        wbvFilename=new Button(shell, SWT.PUSH| SWT.CENTER);
        props.setLook(wbvFilename);
        wbvFilename.setText(Messages.getString("System.Button.Variable"));
        wbvFilename.setToolTipText(Messages.getString("System.Tooltip.VariableToFileOrDir"));
        fdbvFilename=new FormData();
        fdbvFilename.right= new FormAttachment(wbbFilename, -margin);
        fdbvFilename.top  = new FormAttachment(wStepname, margin);
        wbvFilename.setLayoutData(fdbvFilename);

        wFilename=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wFilename.setToolTipText(Messages.getString("AccessOutputDialog.Filename.Tooltip"));
        props.setLook(wFilename);
        wFilename.addModifyListener(lsMod);
        fdFilename=new FormData();
        fdFilename.left = new FormAttachment(middle, margin);
        fdFilename.right= new FormAttachment(wbvFilename, -margin);
        fdFilename.top  = new FormAttachment(wStepname, margin);
        wFilename.setLayoutData(fdFilename);

        wbvFilename.addSelectionListener(VariableButtonListenerFactory.getSelectionAdapter(shell, wFilename));

        // Create file?
        wlCreateFile=new Label(shell, SWT.RIGHT);
        wlCreateFile.setText(Messages.getString("AccessOutputDialog.CreateFile.Label"));
        wlCreateFile.setToolTipText(Messages.getString("AccessOutputDialog.CreateFile.Tooltip"));
        props.setLook(wlCreateFile);
        fdlCreateFile=new FormData();
        fdlCreateFile.left  = new FormAttachment(0, 0);
        fdlCreateFile.top   = new FormAttachment(wFilename, margin);
        fdlCreateFile.right = new FormAttachment(middle, 0);
        wlCreateFile.setLayoutData(fdlCreateFile);
        wCreateFile=new Button(shell, SWT.CHECK);
        wCreateFile.setToolTipText(Messages.getString("AccessOutputDialog.CreateFile.Tooltip"));
        props.setLook(wCreateFile);
        fdCreateFile=new FormData();
        fdCreateFile.left  = new FormAttachment(middle, margin);
        fdCreateFile.top   = new FormAttachment(wFilename, margin);
        fdCreateFile.right = new FormAttachment(100, 0);
        wCreateFile.setLayoutData(fdCreateFile);
        wCreateFile.addSelectionListener(lsSelMod);

		// Table line...
        wbbTablename=new Button(shell, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbTablename);
        wbbTablename.setText(Messages.getString("System.Button.Browse"));
        fdbTablename=new FormData();
        fdbTablename.right= new FormAttachment(100, 0);
        fdbTablename.top  = new FormAttachment(wCreateFile, margin);
        wbbTablename.setLayoutData(fdbTablename);

        wbvTablename=new Button(shell, SWT.PUSH| SWT.CENTER);
        props.setLook(wbvTablename);
        wbvTablename.setText(Messages.getString("System.Button.Variable"));
        wbvTablename.setToolTipText(Messages.getString("System.Tooltip.VariableToFileOrDir"));
        fdvTablename=new FormData();
        fdvTablename.right= new FormAttachment(wbbTablename, -margin);
        fdvTablename.top  = new FormAttachment(wCreateFile, margin);
        wbvTablename.setLayoutData(fdvTablename);

        wlTablename=new Label(shell, SWT.RIGHT);
        wlTablename.setText(Messages.getString("AccessOutputDialog.TargetTable.Label"));
        props.setLook(wlTablename);
        fdlTablename=new FormData();
        fdlTablename.left = new FormAttachment(0, 0);
        fdlTablename.top  = new FormAttachment(wCreateFile, margin);
        fdlTablename.right= new FormAttachment(middle, 0);
        wlTablename.setLayoutData(fdlTablename);

        wTablename=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wTablename.setToolTipText(Messages.getString("AccessOutputDialog.TargetTable.Tooltip"));
 		props.setLook(wTablename);
		fdTablename=new FormData();
        fdTablename.left = new FormAttachment(middle, margin);
        fdTablename.right= new FormAttachment(wbvTablename, -margin);
        fdTablename.top  = new FormAttachment(wCreateFile, margin);
		wTablename.setLayoutData(fdTablename);

        wbvTablename.addSelectionListener(VariableButtonListenerFactory.getSelectionAdapter(shell, wTablename));

		// Create table?
		wlCreateTable=new Label(shell, SWT.RIGHT);
		wlCreateTable.setText(Messages.getString("AccessOutputDialog.CreateTable.Label"));
        wlCreateTable.setToolTipText(Messages.getString("AccessOutputDialog.CreateTable.Tooltip"));
 		props.setLook(wlCreateTable);
		fdlCreateTable=new FormData();
		fdlCreateTable.left  = new FormAttachment(0, 0);
		fdlCreateTable.top   = new FormAttachment(wTablename, margin);
		fdlCreateTable.right = new FormAttachment(middle, 0);
		wlCreateTable.setLayoutData(fdlCreateTable);
		wCreateTable=new Button(shell, SWT.CHECK);
        wCreateTable.setToolTipText(Messages.getString("AccessOutputDialog.CreateTable.Tooltip"));
 		props.setLook(wCreateTable);
		fdCreateTable=new FormData();
		fdCreateTable.left  = new FormAttachment(middle, margin);
		fdCreateTable.top   = new FormAttachment(wTablename, margin);
		fdCreateTable.right = new FormAttachment(100, 0);
		wCreateTable.setLayoutData(fdCreateTable);
		wCreateTable.addSelectionListener(lsSelMod);
        
		// Some buttons
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK"));
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel"));
		
		setButtonPositions(new Button[] { wOK, wCancel }, margin, wCreateTable);

		// Add listeners
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		
		wOK.addListener    (SWT.Selection, lsOK    );
		wCancel.addListener(SWT.Selection, lsCancel);
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
		wTablename.addSelectionListener( lsDef );
		
		wbbTablename.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					getTableName();
				}
			}
		);
        
        // Listen to the Browse... button
        wbbFilename.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e) 
                {
                    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
                    dialog.setFilterExtensions(new String[] {"*.mdb;*.MDB", "*"});

                    if (!Const.isEmpty(wFilename.getText()))
                    {
                        String fname = StringUtil.environmentSubstitute(wFilename.getText());
                        dialog.setFileName( fname );
                    }
                    
                    dialog.setFilterNames(new String[] {Messages.getString("AccessOutputDialog.FileType.AccessFiles"), Messages.getString("System.FileType.AllFiles")});
                    
                    if (dialog.open()!=null)
                    {
                        String str = dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName();
                        wFilename.setText(str);
                    }
                }
            }
        );

        
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		// Set the shell size, based upon previous time...
		setSize();
		
		getData();
		input.setChanged(backupChanged);
	
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}
	
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
        if (input.getFilename()  != null) wFilename.setText(input.getFilename());
		if (input.getTablename() != null) wTablename.setText(input.getTablename());
		
        wCreateFile.setSelection( input.isFileCreated() );
        wCreateTable.setSelection(input.isFileCreated() );
		
		wStepname.selectAll();
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(backupChanged);
		dispose();
	}
	
	private void getInfo(AccessOutputMeta info)
	{
        info.setFilename( wFilename.getText() );
		info.setTablename( wTablename.getText() );
        info.setFileCreated( wCreateFile.getSelection() );
		info.setTableCreated( wCreateTable.getSelection() );
	}
	
	private void ok()
	{
		stepname = wStepname.getText(); // return value
		
		getInfo(input);
		
		dispose();
	}
	
	private void getTableName()
	{
        AccessOutputMeta meta = new AccessOutputMeta();
        getInfo(meta);
        
        Database database = null;
		// New class: SelectTableDialog
        try
        {
            String realFilename = StringUtil.environmentSubstitute(meta.getFilename());
            File file = new File(realFilename);
            
            if (!file.exists() || !file.isFile())
            {
                throw new KettleException(Messages.getString("AccessOutputMeta.Exception.FileDoesNotExist", realFilename));
            }

            database = Database.open(file);
            Set set= database.getTableNames();
            String[] tablenames = (String[]) set.toArray(new String[set.size()]);
            EnterSelectionDialog dialog = new EnterSelectionDialog(shell, props, tablenames, Messages.getString("AccessOutputDialog.Dialog.SelectATable.Title"), Messages.getString("AccessOutputDialog.Dialog.SelectATable.Message"));
            String tablename = dialog.open();
            if (tablename!=null)
            {
                wTablename.setText(tablename);
            }
        }
        catch(Throwable e)
        {
            new ErrorDialog(shell, Messages.getString("AccessOutputDialog.UnableToGetListOfTables.Title"), Messages.getString("AccessOutputDialog.UnableToGetListOfTables.Message"), new Exception(e));
        }
        finally
        {
            // Don't forget to close the bugger.
            try
            {
                if (database!=null) database.close();
            }
            catch(Exception e)
            {
                
            }
        }
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}

}
