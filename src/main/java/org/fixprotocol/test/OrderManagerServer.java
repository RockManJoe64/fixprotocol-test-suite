package org.fixprotocol.test;

import java.text.DecimalFormat;

import org.fixprotocol.test.session.AbstractApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.Text;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Reject;

public class OrderManagerServer extends AbstractApplication implements
		Lifecycle {

	static final Logger log = LoggerFactory.getLogger(OrderManagerServer.class);

	private DecimalFormat decFmt = new DecimalFormat("0000");
	private int currentOrdId = 1;
	private int currentExecId = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	@Handler
	public void handleNewOrderSingle44(quickfix.fix44.NewOrderSingle order,
			SessionID sessionID) {
		Session session = Session.lookupSession(sessionID);
		try {
			quickfix.fix44.ExecutionReport execReport = createExecutionReport(order);
			session.send(execReport);
		} catch (FieldNotFound e) {
			e.printStackTrace();
			Reject reject = new Reject();
			reject.set(new Text(e.getMessage()));
			session.send(reject);
		}
	}

	private quickfix.fix44.ExecutionReport createExecutionReport(
			quickfix.fix44.NewOrderSingle order) throws FieldNotFound {
		quickfix.fix44.ExecutionReport execReport = new ExecutionReport(new OrderID("ORD_" + currentOrdId++),
				new ExecID("EXEC_" + currentExecId++),
				new ExecType(ExecType.NEW),
				new OrdStatus(OrdStatus.PENDING_NEW),
				new Side(order.getSide().getValue()),
				new LeavesQty(0.0),
				new CumQty(0.0),
				new AvgPx(order.getPrice().getValue()));
		execReport.set(order.getClOrdID());
		execReport.set(order.getAccount());
		execReport.set(order.getOrderQty());
		execReport.set(order.getPrice());
		execReport.set(order.getOrdType());
		execReport.set(order.getSymbol());

		return execReport;
	}

	public boolean isRunning() {
		return true;
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
