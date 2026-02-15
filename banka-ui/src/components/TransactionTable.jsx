import React from 'react';

const TransactionTable = ({ transactions }) => {
    if (!transactions || transactions.length === 0) {
        return (
            <div className="empty-state">
                <p>No hay movimientos recientes en esta cuenta.</p>
            </div>
        );
    }

    return (
        <div className="transaction-history">
            <h3>Historial de Movimientos</h3>
            <table className="data-table">
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Descripción</th>
                        <th>Tipo</th>
                        <th className="amount-col">Monto</th>
                        <th className="amount-col">Saldo</th>
                    </tr>
                </thead>
                <tbody>
                    {transactions.map((tx) => (
                        <tr key={tx.id}>
                            <td>{new Date(tx.createdAt).toLocaleDateString()}</td>
                            <td>{tx.description}</td>
                            <td>
                                <span className={`badge ${tx.type.toLowerCase()}`}>
                                    {tx.type}
                                </span>
                            </td>
                            <td className={`amount-col ${tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_IN' ? 'positive' : 'negative'}`}>
                                {tx.type === 'DEPOSIT' || tx.type === 'TRANSFER_IN' ? '+' : '-'}
                                ${tx.amount.toFixed(2)}
                            </td>
                            <td className="amount-col text-neutral">
                                ${tx.newBalance.toFixed(2)}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default TransactionTable;
