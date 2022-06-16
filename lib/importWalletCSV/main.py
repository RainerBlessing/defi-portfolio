import sys
import pandas as pd
import time
import os
import numpy as np
from pathlib import Path

from pandas.io.formats import string


def adaptCSV(var):

    var=[];
    var.append('C:\\Users\\Arthur\\AppData\\Roaming\\defi-portfolio\\transactionData.portfolio')
    var.append('C:\\Users\\Arthur\\Desktop\\Transaktionen_2021-10-10_13-36-24-5.csv')
    pathTransactions = var[0]
    pathWalletCSV = var[1]

    f = open(os.path.dirname(pathWalletCSV) + "\\debuggerFile.txt", "w")
    f.write('Script started' + "\n")

    try:
        f.write(pathTransactions+"\n")
        f.write(pathWalletCSV+"\n")
        time.sleep(1)

        f.write('load Transaction file'+"\n")
        if Path(pathTransactions).is_file():
            transactions = pd.read_csv(pathTransactions, sep=';',engine = 'python')
            transactions = transactions.replace([np.nan], '', regex=True)
            transactions = transactions.replace('', '\'\'', regex=True)
            transactions.columns = ['DD/MM/YYYY (Date) / Time', 'Address','Type', 'Amount','Block Hash', 'Block Height',  'Pool ID','txID']
        else:
            transactions = pd.DataFrame(columns = ['DD/MM/YYYY (Date) / Time', 'Address','Type', 'Amount','Block Hash', 'Block Height',  'Pool ID','txID']);
        walletCSV = pd.read_csv(pathWalletCSV)

        f.write('reorder columns'+"\n")
        #Reorder columns
        walletCSV = walletCSV[['DD/MM/YYYY (Date) / Time', 'Address','Type', 'Amount','Block Hash', 'Block Height',  'Pool ID']]
        walletCSV['txID'] = '\'\''

        f.write('change data format'+"\n")
        # change date format
        date_series = pd.to_datetime(walletCSV['DD/MM/YYYY (Date) / Time'],format="%d/%m/%Y / %I:%M %p")
        index = pd.DatetimeIndex(date_series)
        index = index.astype(np.int64).to_series()/1000000000
        index = index.reset_index(drop="True")
        walletCSV['DD/MM/YYYY (Date) / Time'] = index.astype(int)

        f.write('separate AddPool'+"\n")
        # separeate addpool, remove pool and ppol swaps
        addPoolListe = walletCSV.loc[walletCSV['Type'] == 'AddPoolLiquidity']
        walletCSV = walletCSV.drop(walletCSV[walletCSV['Type'] == 'AddPoolLiquidity'].index)
        for _,addPoolZeile in addPoolListe.iterrows():
          amounts = addPoolZeile['Amount'].split(',')
          addPoolZeile = addPoolZeile.to_frame()
          addPoolZeile = addPoolZeile.transpose()
          for i in amounts:
            addPoolZeile['Amount']=i
            walletCSV= walletCSV.append(addPoolZeile)

        f.write('separate RemovePool'+"\n")
        removePoolListe = walletCSV.loc[walletCSV['Type'] == 'RemovePoolLiquidity']
        walletCSV = walletCSV.drop(walletCSV[walletCSV['Type'] == 'RemovePoolLiquidity'].index)
        for _,removePoolZeile in removePoolListe.iterrows():
          amounts = removePoolZeile['Amount'].split(',')
          removePoolZeile = removePoolZeile.to_frame()
          removePoolZeile = removePoolZeile.transpose()
          for i in amounts:
            removePoolZeile['Amount']=i
            walletCSV= walletCSV.append(removePoolZeile)

        f.write('separate Swaps'+"\n")
        poolSwapListe = walletCSV.loc[walletCSV['Type'] == 'PoolSwap']
        walletCSV = walletCSV.drop(walletCSV[walletCSV['Type'] == 'PoolSwap'].index)
        for _,poolSwapZeile in poolSwapListe.iterrows():
          amounts = poolSwapZeile['Amount'].split(',')
          poolSwapZeile = poolSwapZeile.to_frame()
          poolSwapZeile = poolSwapZeile.transpose()
          for i in amounts:
            poolSwapZeile['Amount']=i
            walletCSV= walletCSV.append(poolSwapZeile)

        f.write('Remove equal transactions'+"\n")
        # remove equal transactions from walletCSV

        walletCSV = walletCSV.drop_duplicates().merge(transactions.drop_duplicates(), on=[ 'Address','Type', 'Amount','Block Hash', 'Block Height'],
                                         how='left', indicator=True)
        walletCSV=walletCSV.loc[walletCSV._merge == 'left_only']
        walletCSV = walletCSV.drop('Pool ID_y', 1)
        walletCSV = walletCSV.drop('txID_y', 1)
        walletCSV = walletCSV.drop('_merge', 1)
        walletCSV = walletCSV.drop('DD/MM/YYYY (Date) / Time_y', 1)
        walletCSV = walletCSV.rename(columns={'Pool ID_x': 'Pool ID', 'txID_x': 'txID','DD/MM/YYYY (Date) / Time_x': 'DD/MM/YYYY (Date) / Time'})

        f.write('Merge DataFrames'+"\n")
        # merge dataframes
        result = walletCSV.append(transactions)

        result = result.sort_values(by=['Block Height'])
        # save csv
        result.to_csv(pathTransactions,  index=False,  header=False, sep=';',line_terminator='\n')


        path = os.path.split(pathTransactions)
        if os.path.exists(path[0]+'\\CSVMerge.cookie'):
          os.remove(path[0]+'\\CSVMerge.cookie')
        f.write('Merge successful')
        f.close()
    except:
        f.write('Jumped to Exception'+"\n")
        f.close()
        path = os.path.split(pathTransactions)
        if os.path.exists(path[0] + '\\CSVMerge.cookie'):
            os.remove(path[0] + '\\CSVMerge.cookie')
        errorFile = open(path[0] + "\\MergingErroroccurred.txt", "w")
        errorFile.close()

if __name__ == '__main__':
    var = sys.argv[1:]
    adaptCSV(var)
