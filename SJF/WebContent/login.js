Ext.onReady(function(){
	//Ext.Msg.alert('Test','Test');
	Ext.BLANK_IMAGE_URL = 'js/ext-3.4.0/resources/images/default/s.gif'; 
    Ext.QuickTips.init();  
    Ext.form.Field.prototype.msgTarget = 'side';
    
    var loginForm = new Ext.FormPanel({  
        title:'Welcome to NTNU SJ System (v0.1)',  
        labelWidth : 60,  
        width : 330,  
        height: 130,  
        frame : true,  
        labelSeparator :':',   
        url: '/SJF/Login.do',
        renderTo: 'form',
        items:[  
            new Ext.form.TextField({  
                fieldLabel:'Account',  
                name : 'userName',  
                allowBlank : false,
                width: 200,
                //vtype:'textfield'  
            }),  
            new Ext.form.TextField({  
                fieldLabel:'Password',  
                name : 'passwd',  
                inputType : 'password',
                width: 200,
                allowBlank : true  
            })  
        ],  
        buttonAlign: 'center',
        buttons:[  
            new Ext.Button({  
                text : 'Login',  
                handler : login  
            }),  
            new Ext.Button({  
                text : 'Reset',  
                handler : reset  
            }),
            new Ext.Button({
            	text: 'Guest',
            	handler: guest
            })
        ]  
    });
    
    function login(){//提交表單  
    	var form = loginForm.getForm();      	
    	/*var conn = new Ext.data.Connection();
        conn.request({
            url: '/SJF/Login.do',
            //此处与params对应，如果为POST，则服务器端从Request.Form中可以取得到数据，反之从QueryString中取数据
            method: 'POST',//GET
            params:form.getValues(),
            success:function(form, action){  
            	if(action.result.errors == null)
            	{
            		Ext.Msg.alert('Hint','Login success!');  
                    document.location='/SJF/Demo/index.jsp';  // 進入登入頁面  
            	}
            	else
            	{
            		Ext.Msg.alert('Hint','Login fail!<br/>'+action.result.errors);  
            	}            	
            },  
            failure:function(form, action){  
                Ext.Msg.alert('Hint','Login fail!, Reason:'+action.failureType);  
            }
        });*/
    	
    	/*var elf = form.getEl().dom;
    	elf.action = "/SJF/Login.do";
    	elf.submit();*/
    	
		form.submit({  
            clientValidation:true,  
            waitMsg : 'Logging, wait...',//提示訊息  
            waitTitle : 'Info',//標題                
            method:'GET',//請求方式  
            url:'/SJF/Login.do',//請求的 url
            success:function(form, action){  
                //Ext.Msg.alert('Hint','Login success!');  
                document.location='/SJF/Demo/index.jsp';  // 進入登入頁面  
            },  
            failure:function(form, action){  
                Ext.Msg.alert('Hint','Login fail!, Reason:'+action.failureType);  
            }  
        });	    	
    }  
    function reset(){  
        loginForm.form.reset();  
    }  
    function guest()
    {
    	document.location = '/SJF/GuestLogin';
    }
});