
//  --------------
function myDrawPie( title , labels , values){
    var pie = new RGraph.Pie('pie', values);
    pie.Set('chart.colors', ['#009933','#ff3300','#3333FF']);
    pie.Set('chart.key', labels);
    pie.Set('chart.key.position', 'gutter');
    pie.Set('chart.gutter', 20);
    pie.Set('chart.linewidth', 2);
    pie.Set('chart.shadow', true);
    pie.Set('chart.strokestyle', '#FFF');
    pie.Draw();
}

function myDrawLine(principalData , interestData , paymentData, commissionData ,xLabels , legend){
    var line = new RGraph.Line("line", [principalData,interestData,paymentData,commissionData]);
    line.Set('chart.background.grid.width', 0.5);
    line.Set('chart.colors', ['#009933','#ff3300','#FF00FF','#3333FF']);
    line.Set('chart.linewidth', 3);
    line.Set('chart.hmargin', 2);
    line.Set('chart.labels', xLabels);
    line.Set('chart.gutter', 40);
    line.Set('chart.text.size', 8);
    line.Set('chart.key', legend);
    line.Set('chart.key.shadow', true);
    line.Draw();
}


function myDrayAll(){
    try{
        myDrawPie(
            window.schedule.getPieTitle(),
            JSON.parse( window.schedule.getPieLabels()),
            JSON.parse( window.schedule.getPieValues())
        );
        myDrawLine(
            JSON.parse(window.schedule.getPrincipalPointsData()),
            JSON.parse(window.schedule.getInterestPointsData()),
            JSON.parse(window.schedule.getPaymentPointsData()),
            JSON.parse(window.schedule.getCommissionPointsData()),
            JSON.parse(window.schedule.getXLabels()),
            JSON.parse(window.schedule.getLegend())
        );
    }catch( err ){
    }

}

window.onload = function (){
 myDrayAll();
};



