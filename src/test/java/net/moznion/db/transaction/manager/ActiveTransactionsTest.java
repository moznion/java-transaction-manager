package net.moznion.db.transaction.manager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * Test for check trace information of active transactions.
 *
 * @author moznion
 */
public class ActiveTransactionsTest extends TestBase {
    @Test
    public void proveActiveTransactions() throws SQLException {
        final long currentThreadId = Thread.currentThread().getId();
        final String filename = "ActiveTransactionsTest.java";

        TransactionManager txnManager = new TransactionManager(connection);

        assertEquals(0, txnManager.getActiveTransactions().size());

        try (TransactionScope txn1 = new TransactionScope(txnManager)) {
            {
                List<TransactionTraceInfo> activeTransactions = txnManager
                        .getActiveTransactions();
                assertEquals(1, activeTransactions.size());

                TransactionTraceInfo got = activeTransactions.get(0);
                assertEquals(
                        "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                        got.getClassName());
                assertEquals(filename, got.getFileName());
                assertEquals("proveActiveTransactions", got.getMethodName());
                assertEquals(25, got.getLineNumber());
                assertEquals(currentThreadId, got.getThreadId());
                assertEquals(
                        String.format(
                                "File Name: %s, Class Name: %s, Method Name: %s, Line Number: %d, Thread ID: %d",
                                filename,
                                "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                                "proveActiveTransactions", 25, currentThreadId),
                        got.toString());
            }

            try (TransactionScope txn2 = new TransactionScope(txnManager)) {
                txnManager.txnBegin();
                {
                    List<TransactionTraceInfo> activeTransactions = txnManager
                            .getActiveTransactions();
                    assertEquals(3, activeTransactions.size());

                    TransactionTraceInfo got = activeTransactions.get(2);
                    assertEquals(
                            "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                            got.getClassName());
                    assertEquals(filename, got.getFileName());
                    assertEquals("proveActiveTransactions", got.getMethodName());
                    assertEquals(49, got.getLineNumber());
                    assertEquals(currentThreadId, got.getThreadId());
                }
                txnManager.txnCommit();

                List<TransactionTraceInfo> activeTransactions = txnManager
                        .getActiveTransactions();
                assertEquals(2, activeTransactions.size());

                TransactionTraceInfo got = activeTransactions.get(1);
                assertEquals(
                        "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                        got.getClassName());
                assertEquals(filename, got.getFileName());
                assertEquals("proveActiveTransactions", got.getMethodName());
                assertEquals(48, got.getLineNumber());
                assertEquals(currentThreadId, got.getThreadId());

                txn2.commit();
            }

            {
                List<TransactionTraceInfo> activeTransactions = txnManager
                        .getActiveTransactions();
                assertEquals(1, activeTransactions.size());

                TransactionTraceInfo got = activeTransactions.get(0);
                assertEquals(
                        "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                        got.getClassName());
                assertEquals(filename, got.getFileName());
                assertEquals("proveActiveTransactions", got.getMethodName());
                assertEquals(25, got.getLineNumber());
                assertEquals(currentThreadId, got.getThreadId());
            }

            txnManager.txnBegin();
            {
                List<TransactionTraceInfo> activeTransactions = txnManager
                        .getActiveTransactions();
                assertEquals(2, activeTransactions.size());

                TransactionTraceInfo got = activeTransactions.get(1);
                assertEquals(
                        "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                        got.getClassName());
                assertEquals(filename, got.getFileName());
                assertEquals("proveActiveTransactions", got.getMethodName());
                assertEquals(97, got.getLineNumber());
                assertEquals(currentThreadId, got.getThreadId());
            }
            txnManager.txnCommit();

            txnManager.txnBegin();
            {
                List<TransactionTraceInfo> activeTransactions = txnManager
                        .getActiveTransactions();
                assertEquals(2, activeTransactions.size());

                TransactionTraceInfo got = activeTransactions.get(1);
                assertEquals(
                        "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                        got.getClassName());
                assertEquals(filename, got.getFileName());
                assertEquals("proveActiveTransactions", got.getMethodName());
                assertEquals(114, got.getLineNumber());
                assertEquals(currentThreadId, got.getThreadId());
            }
            txnManager.txnRollback();
        } // auto rollback

        txnManager.txnBegin();
        {
            List<TransactionTraceInfo> activeTransactions = txnManager
                    .getActiveTransactions();
            assertEquals(1, activeTransactions.size());

            TransactionTraceInfo got = activeTransactions.get(0);
            assertEquals(
                    "net.moznion.db.transaction.manager.ActiveTransactionsTest",
                    got.getClassName());
            assertEquals(filename, got.getFileName());
            assertEquals("proveActiveTransactions", got.getMethodName());
            assertEquals(132, got.getLineNumber());
            assertEquals(currentThreadId, got.getThreadId());
        }
        txnManager.txnCommit();

        assertEquals(0, txnManager.getActiveTransactions().size());
    }
}
