/*!
 * Copyright(c) 2013-2020 NTNU
 */
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = '../js/ext-3.1.1/resources/images/default/s.gif';
	Ext.chart.Chart.CHART_URL = '../js/ext-3.1.1/resources/charts.swf';
	Ext.QuickTips.init();
	var latestNews=null;
	
	var accountMenu = new Ext.menu.Menu({                    
        items: [    
                {text: 'Logout', handler: logout, iconCls:'logoutIcon'}    
               ]    
    });	 
	
	var reportMenu = new Ext.menu.Menu({
		items:[
		       {text: 'Error Category Dist', handler: ferrReport, iconCls:'statR1Icon'}
		       ]
	});

	
	var langStore = new Ext.data.SimpleStore({//定義組合框顯示的數據源  
        fields: ['subject', 'topic'],  
        data : [['繁體中文','NTNU.TSENT'],['簡體中文','NTNU.SENT']]  
    });
	
	
	
	var submitForm = new Ext.form.FormPanel({
        baseCls: 'x-plain',
        labelWidth: 55,
        url:'submit.jsp',
        region: 'west',
        width: 500,
        collapsible: true,
        margins:'3 0 3 3',
        cmargins:'3 3 3 3',
        defaultType: 'textfield',

        items: [
            {
            	xtype: 'textarea',
            	hideLabel: true,
            	name: 'article',
            	id: 'article',
            	anchor: '100% -60'// anchor width by percentage and height by raw adjustment
        	},
        	{
        		xtype: 'radiogroup',
        		fieldLabel: '語言',
        		items: [        		        
        			    {boxLabel: '繁體中文', name: 'topic', inputValue: 'NTNU.TSENT', checked: true},        			    
        			    {boxLabel: '簡體中文', name: 'topic', inputValue: 'NTNU.SENT'}
        			   ]
        	}
        	/*new Ext.form.Radio({  
                name : 'subject',//名字相同的單選框會是為同一組  
                fieldLabel:'語言',  
                value:'NTNU.TSENT',
                boxLabel : '繁體中文',
                hiddenName: 'topic',
                checked: true
            }),  
            new Ext.form.Radio({  
                name : 'subject',//名字相同的單選框會是為同一組  
                fieldLabel:'語言', 
                value:'NTNU.SENT',  
                hiddenName: 'topic',
                boxLabel : '簡體中文'  
            })*/
            
        	/*new Ext.form.ComboBox({  
                //id:'subject',  
                fieldLabel:'語言',  
                triggerAction: 'all',//單觸擊顯示全部數據  
                store : langStore,//設置數據源  
                displayField:'subject',//定義要顯示的字段  
                valueField:'topic',//定義值字段  
                mode: 'local',//本地模式  
                forceSelection : true,//輸入值必須再列表存在  
                resizable : true,//允許改變組合框大小  
                typeAhead : true,//允許匹配剩餘文本  
                value:'NTNU.SENT',//默認選擇 簡體中文 
                hiddenName: 'topic',
                handleHeight : 10//下拉列表托拉手柄高度                
            })*/
        ],
        buttons: [{
            text: 'Submit',
            handler: login
        },{
            text: 'Reset',
            handler: reset
        }]
    });
    	
	// tabs for the center
    var tabs = new Ext.TabPanel({
        region: 'center',
        margins:'3 3 3 0', 
        activeTab: 0,
        split: true,
        defaults:{autoScroll:true},       
        items:[{
        	iconCls: 'newsIcon',
            title: 'News',
            id: 'News',
            autoLoad :'news.html',
            buttons: [{
                text: 'Update',
                handler: updateNews
            }]
        }]
    });
    //tabs.add(newsPanel).show();

    function test() // Testing
    {
    	//var t = Ext.getCmp('article'), t1 = t.getEl().down('textarea');
    	//var t = submitForm.getComponent('article');
		// http://www.sencha.com/forum/showthread.php?82135-Get-form-field-by-name
		// http://www.sencha.com/forum/showthread.php?226543-%C2%A0How-to-dynamically-change-the-background-color-of-a-text-area-field
		var artField = submitForm.getForm().findField("article");
		var oriValue = artField.getValue();
		//artField.setValue('<span class="UNDERLINE">欲有底線的文字</span>, 沒有底線的文字.');
		
		var tabPage = tabs.add({//動態添加tab頁  
            title: 'Test', 
            iconCls: 'watchIcon',
            html : '<span class="UNDERLINE">欲有底線的文字</span>, 沒有底線的文字.',  
            closable : true//允許關閉  
        });  
        tabs.setActiveTab(tabPage);//設置當前tab頁
    	Ext.Msg.alert('Test', 'test: '+oriValue);	
    }

    
    var statStore = new Ext.data.Store({
		//autoLoad: true,
		reader: new Ext.data.JsonReader(),
		sortInfo: {field: 'count', direction: 'DESC'} ,
		proxy: new Ext.data.HttpProxy({
			url: '/SJF/Services/TopNCate'
		})
	});
    //statStore.load();
    
    /*var task = {
		    run: updateStatChart,
		    interval: 600000 //10 second
	};    
	var runner = new Ext.util.TaskRunner();
	runner.start(task);
	Ext.TaskMgr.start({
	    run: updateNews,
	    interval: 600000
	});*/
	

    // extra extra simple
	var lineChart = null;         
    
    
	// http://jsfiddle.net/rixo/3CZr2/
	// http://stackoverflow.com/questions/5926602/extjs-two-panel-resize-accordingly-when-window-resizes
    var win = new Ext.Window({
        title: 'SJ Window',
        //closable:true,
        width:1200,
        height:550,
        //border:false,
        constrain: true,  
        maximizable: true,
        plain:true,
        layout: 'border',
        tbar: [
               {text:'Account', menu:accountMenu, iconCls:'accountIcon'},
               {text:'Report', menu:reportMenu, iconCls:'reportIcon'}
              ],

        items: [submitForm, tabs]
    });
	
    win.show(this);
    win.setPosition(30, 30);  
    
    function aj(anchor)
    {
    	//var jumpLoc = document.location.href+anchor;
    	Ext.Msg.alert("Test", "Jump to "+anchor);
    }
    
    function ferrReport()
    {
    	lineChart = new Ext.chart.ColumnChart({
        	store: statStore,
        	xField: 'category',
        	yField: 'count',
        	listeners: {
    			itemclick: function(o){
    				var rec = store.getAt(o.index);
    				Ext.example.msg('Item Selected', 'You chose {0}.', rec.get('name'));
    			}
    		}
        });
    	var statPanel = new Ext.Panel({
            title: 'ECD',       
            width:500,
            height:300,
            layout:'fit',
            iconCls:'statR1Icon',
            closable : true, //允許關閉  
            items: [lineChart],
            buttons:[  
                new Ext.Button({  
                      text:'Refresh',
                      handler:refreshStatChart
                })  
            ]
        });
    	tabs.add(statPanel).show();
    	statStore.load();
        lineChart.show();
    }
    
    function updateStatChart()
    {
    	statStore.load();
    	lineChart.show();
    }
    
    function updateNews()
    {
    	Ext.Ajax.request({
            url: '/SJF/Demo/news.html', // you must have access to the server
            method: 'GET',
            success: function(response)  
            {                  
                var newsTab = tabs.getItem('News');
                newsTab.update(response.responseText);
                //Ext.Msg.alert('Success', response.responseText);
            },
            failure: function(response)  
            {  
                //Ext.Msg.alert('Fail', response.responseText); 
            	newsTab.update("Server error: "+response.responseText);
            }
        });
    }
    
    function refreshStatChart()
    {    	    	    	    	
    	statStore.load();
    	//lineChart.getView().refresh();
    	lineChart.show();
    	Ext.Msg.alert('Info', "Refreshing...Done!");    	
    }
    
    
    function login(){//提交表單  (Evaluation sentences)
    	submitForm.form.submit({  
            clientValidation:true,  
            waitMsg : '正在檢視, 請稍候...',//提示訊息  
            waitTitle : '提示',//標題  
            url : '/SJF/Services/EvalSentServlet',//請求的 url  
            //method:'POST',//請求方式  
            success:function(form, action){  
                Ext.Msg.alert('提示','檢視完畢<br/>進入結果頁面...');  
                //document.location='result.jsp';  // 進入登入頁面
                var index = tabs.items.length + 1;  
                var tabPage = tabs.add({//動態添加tab頁  
                    title: 'Result'+index, 
                    iconCls: 'watchIcon',
                    html : action.result.resp,  
                    closable : true//允許關閉  
                });  
                tabs.setActiveTab(tabPage);//設置當前tab頁
                //Ext.Msg.alert('Feedback', action.result.feedback);
                var fids = new String(action.result.feedback).split(',');
                for (var i=0; i < fids.length; i++)
                {
                	var fid = fids[i];
                	var elm_g = Ext.get(fid+'_good');
                	var elm_b = Ext.get(fid+'_bad');
                	if (typeof(elm_g) == 'undefined' || elm_g == null ||
                		typeof(elm_b) == 'undefined' || elm_b == null)  
                	{
                		 Ext.Msg.alert('Error', 'ID="'+fid+'" does not exist!');
                	}
                	else 
                	{
                		//alert('Has ID="'+fid+'"');
                		var elm_es = Ext.get('err_'+fid);
                		if(typeof(elm_es) != 'undefined' && elm_es != null)
                		{
                			new Ext.ToolTip({
                    	        target: 'err_'+fid,
                    	        html: 'Check Below <b>Item-'+(i+1)+"</b>",
                    	        title: 'Tooltip',
                    	        autoHide: true,                	        
                    	        draggable:true,
                    	        trackMouse:true
                    	    });
                		}
                		elm_g.on("click", feedbackGood, this, fid);
                		elm_b.on("click", feedbackBad, this, fid);
                		new Ext.ToolTip({
                	        target: fid+'_good',
                	        html: 'Click to feedback (good)...',
                	        title: 'Tooltip',
                	        autoHide: true,                	        
                	        draggable:true,
                	        trackMouse:true
                	    });
                		new Ext.ToolTip({
                	        target: fid+'_bad',
                	        html: 'Click to feedback (bad)...',
                	        title: 'Tooltip',
                	        autoHide: true,                	        
                	        draggable:true,
                	        trackMouse:true
                	    });
                	}
                	
                }                                 
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
    
    function logout()
    {
    	document.location = '/SJF/Logout';
    }
    
    function reset(){  
    	submitForm.form.reset();  
    	Ext.Ajax.request({  
		    url: '/SJF/Services/RetireSession',  
		    success: function(response)  
		    {  		        
		        var jsonObj = Ext.decode(response.responseText);
		        //console.log(decodedString);
		        Ext.Msg.alert('Test', jsonObj.resp);  
		    },  
		    failure: function(response)  
		    {  
		        Ext.Msg.alert('Fail', response.responseText);  
		    },  
		    params: { feedback: elm.getValue(), id: fid.id }  
		});  
    } 
    
    function feedbackGood(scope, fid)
    {
    	//Ext.Msg.alert('Info', 'Feedback good!');
    	Ext.Ajax.request({  
		    url: '/SJF/Services/FeedbackServlet',  
		    success: function(response)  
		    {  		        
		        var jsonObj = Ext.decode(response.responseText);		       
		        Ext.Msg.alert('Success', jsonObj.resp);  
		    },  
		    failure: function(response)  
		    {  
		        Ext.Msg.alert('Fail', response.responseText);  
		    },  
		    params: { feedback: "+1", id: fid.id }  
		});
    }
    
    function feedbackBad(scope, fid)
    {
    	//Ext.Msg.alert('Info', 'Feedback bad!');
    	Ext.Ajax.request({  
		    url: '/SJF/Services/FeedbackServlet',  
		    success: function(response)  
		    {  		        
		        var jsonObj = Ext.decode(response.responseText);		        
		        Ext.Msg.alert('Success', jsonObj.resp);  
		    },  
		    failure: function(response)  
		    {  
		        Ext.Msg.alert('Fail', response.responseText);  
		    },  
		    params: { feedback: "-1", id: fid.id }  
		});
    }
    
    function feedback(scope, fid)
    {    	
    	// HTML DOM 快速導覽 - 元素物件 <input> (HTMLInputElement) - http://pydoing.blogspot.tw/2011/12/javascript-element-input.html
    	var elm_id = fid.id+"_text";
    	var elm = Ext.get(elm_id);
    	if (typeof(elm) == 'undefined' || elm == null)  
    	{
    		Ext.Msg.alert('Info','Feedback error! '+elm_id);     		
    	}
    	else
    	{
    		//Ext.Msg.alert('Info','Feedback ('+elm_id+'):<br/>'+elm.getValue());     
    		Ext.Ajax.request({  
    		    url: '/SJF/Services/FeedbackServlet',  
    		    success: function(response)  
    		    {  
    		        //Ext.Msg.alert('Success', response.responseText);  
    		        var jsonObj = Ext.decode(response.responseText);
    		        //console.log(decodedString);
    		        Ext.Msg.alert('Success', jsonObj.resp);  
    		    },  
    		    failure: function(response)  
    		    {  
    		        Ext.Msg.alert('Fail', response.responseText);  
    		    },  
    		    params: { feedback: elm.getValue(), id: fid.id }  
    		});  
    	}    	  
    }       
});