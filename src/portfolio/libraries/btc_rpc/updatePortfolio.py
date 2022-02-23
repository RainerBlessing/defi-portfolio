import csv
import sys

from bitcoinrpc.authproxy import AuthServiceProxy
from pathlib import Path
import pandas as pd
import os
import numpy as np
import warnings
import platform
import pathlib
import requests

warnings.simplefilter(action='ignore', category=FutureWarning)
pd.options.mode.chained_assignment = None

if __name__ == '__main__':

    if False:
        if platform.system() == 'Linux' or platform.system() == 'Windows':
            pathPortfolioData = sys.argv[1]
            pathConfig = sys.argv[2]
        else:
            pathPortfolioData = sys.argv[0]
            pathConfig = sys.argv[0]
            pathPortfolioData = pathPortfolioData.replace("/updatePortfolio", "")  # match mac os path
            pathConfig = pathPortfolioData.replace("/updatePortfolio", "") + "/defi.conf"  # match mac os path
 #   pathPortfolioData = os.environ.get("APPDATA") + '\\defi-portfolio'
 #   pathConfig = 'C:\\Users\\Arthur\\Desktop\\defiJava\\PortfolioData\\defi.conf'

    pathPortfolioData = "C:\\Users\\danie\\AppData\\Roaming\\defi-portfolio"
    # Addresse
    addresses = pd.read_csv(open(pathPortfolioData + '/Addresses.csv'), header = None)
#################
    local = 0

    if local == 0:
        data = pd.DataFrame()

        for iAddress in range(0, addresses.__len__()):
            rewards = pd.read_json(requests.get("https://api.dfi.tax/p01/rwd/"+addresses.at[iAddress, 0]+"/day/EUR").content)
            transactions1 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/"+addresses.at[iAddress, 0]+"/2022/EUR").content)
            transactions2 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/" + addresses.at[iAddress, 0] + "/2021/EUR").content)
            transactions3 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/" + addresses.at[iAddress, 0] + "/2020/EUR").content)
    else:
        data = pd.read_json('C:/Users/Arthur/Desktop/test.json')

    # Aufsplittung add/remove Pool und poolswap
    if data.__len__() != 0:
        swap = data.loc[data['type'] == 'PoolSwap']
        data.drop(data.loc[data['type'] == 'PoolSwap'].index, inplace=True)
        addPool = data.loc[data['type'] == 'AddPoolLiquidity']
        data.drop(data.loc[data['type'] == 'AddPoolLiquidity'].index, inplace=True)
        removePool = data.loc[data['type'] == 'RemovePoolLiquidity']
        data.drop(data.loc[data['type'] == 'RemovePoolLiquidity'].index, inplace=True)
        swapAdd = swap.append(addPool)
        swapAddRemove = swapAdd.append(removePool)
        swapAddRemoveSingles = pd.DataFrame()
        for i in range(0,swapAddRemove.__len__()):
            amounts = swapAddRemove.iloc[i]['amounts']
            for iAmount in range(0,amounts.__len__()):
                swapAddRemove.iloc[i,7]=amounts[iAmount]
                swapAddRemoveSingles = swapAddRemoveSingles.append(swapAddRemove.iloc[i])
        data = data.append(swapAddRemoveSingles)

        data['blockTime'] = data['blockTime'].astype(int)
        data['blockHeight'] = data['blockHeight'].astype(int)


        # Aufsplittung dfi und betrag
        splittedAmount = []
        splittedCoin = []
      #  rawDataAmount = []
        rawData = data
        for i in data['amounts']:
            if isinstance(i, list):
                splitted = i[0].split('@')
            else:
                splitted = i.split('@')
            splittedAmount.append(float(splitted[0]))
            splittedCoin.append(splitted[1])
      #      rawDataAmount.append( str(i).replace('[\'', '').replace('\']', ''))
        # export all single data
        # rawData['amounts']=rawDataAmount
        # rawData = rawData.sort_values(by=['blockTime'],ascending=True)
        # rawData = rawData.fillna('_')
        #
        # if 'txid' in list(rawData.columns.values) and 'poolID' not in list(rawData.columns.values):
        #     rawData['poolID'] = '_'
        # elif 'txid' not in list(rawData.columns.values) and 'poolID' in list(rawData.columns.values):
        #     rawData['txid'] = '_'
        # elif 'txid' not in list(rawData.columns.values) and 'poolID' not in list(rawData.columns.values):
        #     rawData['poolID'] = '_'
        #     rawData['txid'] = '_'
        # if 'rewardType' in list(rawData.columns.values):
        #     rawData = rawData[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid",'rewardType']]
        # else:
        #     rawData = rawData[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid"]]
        #
        # rawData.to_csv(pathPortfolioData + '/rawData.portfolio', mode='a', header=False, sep=';', index = False)
        ###############

        data.insert(len(data.columns), 'Amount', splittedAmount)
        data.insert(len(data.columns), 'Coin', splittedCoin)
        data=data.drop(columns='amounts')


        # split dataFrame into rewards & commissions  and  rest
        dataRewCom = data.query('type == "Rewards" or type == "Commission"')
        dataRest = data.query('type != "Rewards" and type != "Commission"')

        # Umwanldung in dataframe
        dataRewCom['blockTime'] = pd.to_datetime(dataRewCom['blockTime'], unit='s').dt.date
        # sum amounts by date
        if 'poolID' in list(dataRewCom.columns.values):
            if 'rewardType' in list(dataRewCom.columns.values):
                dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'poolID', 'Coin'], as_index=False).agg(
                    {'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum', 'rewardType': 'first'})
            else:
                dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'poolID', 'Coin'], as_index=False).agg(
                    {'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum'})
        else:
            if 'rewardType' in list(dataRewCom.columns.values):
                dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'Coin'], as_index=False).agg({'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum','rewardType':'first'})
            else:
                dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'Coin'], as_index=False).agg({'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum'})





       # date time to unix timestamp
        index = pd.DatetimeIndex(dataRewCom['blockTime'])
        index = index.astype(np.int64).to_series() / 1000000000
        index = index.reset_index(drop="True")
        dataRewCom['blockTime'] = index.astype(int)
        dataRewCom['blockTime'] = dataRewCom['blockTime'] + (24*60*60)-1

        data = dataRewCom.append(dataRest)

        data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
        data = data.drop(columns=['Amount','Coin'])
        data = data.reset_index()

        # reorder columns

        if 'txid' in list(data.columns.values) and 'poolID' not in list(data.columns.values):
            data['poolID'] = '_'
        elif 'txid' not in list(data.columns.values) and 'poolID' in list(data.columns.values):
            data['txid'] = '_'
        elif 'txid' not in list(data.columns.values) and 'poolID' not in list(data.columns.values):
            data['poolID'] = '_'
            data['txid'] = '_'
        if 'rewardType' in list(data.columns.values):
             data = data[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid",'rewardType']]
        else:
            data = data[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid"]]

        # save to transaction.portfolio
        data = data.fillna('_')
        data = data.sort_values(by=['blockTime'],ascending=True)

        # add to transactio.portfolio
        data.to_csv(pathPortfolioData + '/transactionData.portfolio', mode='a', header=False, sep=';', index = False)

    os.remove(pathPortfolioData + '/pythonUpdate.portfolio')