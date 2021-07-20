import sys

import numpy as np
import pandas as pd
import time
import datetime

def adaptCSV(var):
  #  pathTransactions = 'C:\\Users\\Arthur\\AppData\\Roaming\\defi-portfolio\\transactionData.portfolio'
  #  pathWalletCSV = 'C:\\Users\\Arthur\\Desktop\\Transactions\\Transactions_2021-7-16_23-43-12.csv'

    pathTransactions = var[0]
    pathWalletCSV = var[1]
    print(pathTransactions)
    print(pathWalletCSV)
    time.sleep(1)
    transactions = pd.read_csv(pathTransactions, sep=';',engine = 'python')
    transactions = transactions.replace([np.nan], '', regex=True)
    transactions = transactions.replace('', '\'\'', regex=True)
    transactions.columns = ['DD/MM/YYYY (Date) / Time', 'Address','Type', 'Amount','Block Hash', 'Block Height',  'Pool ID','txID']

    walletCSV =  pd.read_csv(pathWalletCSV)

    #Reorder columns
    walletCSV = walletCSV[['DD/MM/YYYY (Date) / Time', 'Address','Type', 'Amount','Block Hash', 'Block Height',  'Pool ID']]
    walletCSV['txID'] = '\'\''

    # change date format
    date_series = pd.to_datetime(walletCSV['DD/MM/YYYY (Date) / Time'])
    index = pd.DatetimeIndex(date_series)
    index = index.astype(np.int64).to_series()/1000000000
    index = index.reset_index(drop="True")
    walletCSV['DD/MM/YYYY (Date) / Time'] = index.astype(int)

    # separeate addpool remove pool
    a = walletCSV.loc[walletCSV['Type'] == 'AddPoolLiquidity']
    a = walletCSV.loc[walletCSV['Type'] == 'RemovePoolLiquidity']
    a = walletCSV.loc[walletCSV['Type'] == 'PoolSwap']


    # merge dataframes unequal transactions
    frames = [walletCSV, transactions]
    result = walletCSV.append(transactions)

    # save csv
    result.to_csv('C:\\Users\\Arthur\\AppData\\Roaming\\defi-portfolio\\transactionData.portfolio',  index=False,  header=False, sep=';',line_terminator='\n')


if __name__ == '__main__':
    var = sys.argv[1:]
    adaptCSV(var)
