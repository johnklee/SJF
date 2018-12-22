/*!
 * Ext JS Library 3.1.1
 * Copyright(c) 2006-2010 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.onReady(function() {
    var submitForm = new Ext.form.FormPanel({
        baseCls: 'x-plain',
        labelWidth: 55,
        url:'submit.jsp',
        defaultType: 'textfield',

        items: [{
            fieldLabel: 'Subject',
            name: 'subject',
            anchor: '100%'  // anchor width by percentage
        }, {
            xtype: 'textarea',
            hideLabel: true,
            name: 'article',
            anchor: '100% -60'  // anchor width by percentage and height by raw adjustment
        }]
    });

    var window = new Ext.Window({
        title: 'Resize Me',
        applyTo: 'hello-win',
        width: 700,
        height:500,
        minWidth: 300,
        minHeight: 200,
        layout: 'fit',
        draggable: true,
        plain:true,        
        modal:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: submitForm,

        buttons: [{
            text: 'Submit',
            handler: login
        },{
            text: 'Reset',
            handler: reset
        }]
    });

    window.show();
    
    function login(){//提交表單  
    	submitForm.form.submit({  
            clientValidation:true,  
            waitMsg : '正在檢視, 請稍候...',//提示訊息  
            waitTitle : '提示',//標題  
            url : '/SJF/EvalSentServlet',//請求的 url  
            //method:'POST',//請求方式  
            success:function(form,action){  
                Ext.Msg.alert('提示','檢視完畢<br/>進入結果頁面...');  
                document.location='result.jsp';  // 進入登入頁面  
            },  
            failure:function(form,action){  
            	switch (action.failureType) {
                case Ext.form.Action.CLIENT_INVALID:
                    Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');
                    break;
                case Ext.form.Action.CONNECT_FAILURE:
                    Ext.Msg.alert('Failure', 'Ajax communication failed');
                    break;
                case Ext.form.Action.SERVER_INVALID:                    
                    Ext.Msg.alert('錯誤','文章檢視失敗!,<br/> 原因：'+action.result.msg); 
                }
                 
            }  
        });  
    } 
    function reset(){  
    	submitForm.form.reset();  
    } 
});