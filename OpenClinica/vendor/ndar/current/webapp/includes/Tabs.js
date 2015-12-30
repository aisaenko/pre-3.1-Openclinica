// New tab functions 8-8-06

var TabValue = 0;
var PrevTabNumber = 0;

	  
function IncreaseTabValue() 
	{
	TabValue = TabValue+1;
	}
	

function DecreaseTabValue() 
	{
	TabValue = TabValue-1;
	}

function AdvanceTabs()
	{
	leftnavExpand('Tab' + TabValue); 
	leftnavExpand('Tab' + (TabValue+TabsShown));
	}

function EnableArrows()
	{

	if (TabValue==0)
		{
		leftnavExpand('TabsBackDis');
		leftnavExpand('TabsBack');
		}
	else {}

	if (TabValue==TabsNumber-(TabsShown+1))
		{
		leftnavExpand('TabsNextDis');
		leftnavExpand('TabsNext');
		}
	else {}

}

function HighlightTab(TabNumber)
	{
	leftnavExpand('Tab' + TabNumber + 'NotSelected'); 
	leftnavExpand('Tab' + TabNumber + 'Selected');
	leftnavExpand('Table' + TabNumber);

	if (PrevTabNumber>=0)
		{
		leftnavExpand('Tab' + PrevTabNumber + 'NotSelected'); 
		leftnavExpand('Tab' + PrevTabNumber + 'Selected');
		leftnavExpand('Table' + PrevTabNumber);
		}

	PrevTabNumber=TabNumber
	}

function TabsBack()
	{
	AdvanceTabs();
	DecreaseTabValue();
	EnableArrows();
	}

function TabsForward()
	{
	EnableArrows();
	IncreaseTabValue();
	AdvanceTabs();
	}
	
function TabsForwardByNum(num)
	{
	var i=1;
	while (i<num){
	TabsForward();
	i++;
	}
	}	

function DisplayTabs()
	{
	TabID=1;

	while (TabID<=TabsNumber)
		{
		if (TabID<=TabsShown)
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all" width="180">');
			}
		else
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none" width="180">');
			}
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
		document.write('<a class="tabtext" href="javascript:HighlightTab(' + TabID + ');">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
	
		TabID++
		}
	}

function HideGroups(TableID, GroupsColumns,GroupsRows)
	{
	leftnavExpand('HideGroups');
	leftnavExpand('ShowGroups');
	ColumnNumber=0;
	RowNumber=0;
	while (ColumnNumber<=GroupsColumns)
		{
		while (RowNumber<=GroupsRows)
			{
			leftnavExpand('Groups_' + TableID + '_' + ColumnNumber + '_' + RowNumber)
			RowNumber++
			}
		RowNumber=0;
		ColumnNumber++
		}
	}
	
