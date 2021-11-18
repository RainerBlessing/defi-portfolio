import sys

from bitcoinrpc.authproxy import AuthServiceProxy
import pandas as pd
import os
import numpy as np

def create_connection_rpc(cred):
    url = "http://%s:%s@%s:%s/"%(cred['rpc_username'], cred['rpc_password'], cred['rpc_hostname'], cred['rpc_port'])
    return AuthServiceProxy(url)

def get_history():
    poolpairs = rpc_connection.listaccounthistory("dFDKi7d4nHpSqG3vJjPtQ7AmGLaHkH3Fto",{"maxBlockHeight": 900000,"depth":2000,"no_rewards":False,"limit":2000})
    return poolpairs

if __name__ == '__main__':
   # var = sys.argv[1:]
   # credentials = var[1]
   # listAdresses = var[2]

    # In Zukunt über Übergabeparamter
    credentials = {}
    credentials['rpc_username'] = 'bla'
    credentials['rpc_password'] = 'blabla'
    credentials['rpc_port'] = '8555'
    credentials['rpc_hostname'] = '127.0.0.1'

    rpc_connection = create_connection_rpc(credentials)
    new_block = rpc_connection.getblockcount()
    poolpairs = get_history()




    data = pd.DataFrame(poolpairs)
    # Umwanldung in dataframe
    data['blockTime'] = pd.to_datetime(data['blockTime'], unit='s').dt.date
    splittedAmount = []
    splittedCoin = []
    # Aufsplittung dfi und betrag
    for i in data['amounts']:
        splitted = i[0].split('@')
        splittedAmount.append(float(splitted[0]))
        splittedCoin.append(splitted[1])

    splittedCoin = splittedCoin.pop(0)
    splittedAmount = splittedAmount.pop(0)
    data.insert(len(data.columns), 'Amount', splittedAmount)
    data.insert(len(data.columns), 'Coin', splittedCoin)
    data=data.drop(columns='amounts')

    data = data.groupby(data['blockTime']).agg({'Amount': 'sum', 'Coin': 'first','owner':'last','blockHeight':'first','blockHash':'first','type':'first','poolID':'first','Coin':'first'})
    data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
    data = data.drop(columns=['Amount','Coin'])
    data = data.reset_index()

    index = pd.DatetimeIndex(data['blockTime'])
    index = index.astype(np.int64).to_series() / 1000000000
    index = index.reset_index(drop="True")
    data['blockTime'] = index.astype(int)

    data.to_csv(os.environ.get("APPDATA")+'\\defi-portfolio\\transaction.portfolio', sep=';')
