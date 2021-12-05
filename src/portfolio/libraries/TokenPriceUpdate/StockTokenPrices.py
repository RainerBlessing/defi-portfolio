import yfinance as yf
from datetime import date
import pandas
import numpy
import os

if __name__ == '__main__':
  #  pathPortfolioData = sys.argv[1]
    pathPortfolioData = os.environ.get("APPDATA") + '\\defi-portfolio'

    today = date.today()
    strDate = today.strftime("%Y-%m-%d")
    strStartDate = '2021-11-01'
    Tokens = ['TSLA', 'GME', 'GOOGL', 'BABA', 'PLTR', 'AAPL', 'SPY', 'QQQ', 'PDBC', 'VNQ', 'ARKK', 'GLD', 'URTH', 'TLT',
              'SLV']
    resultUSD = pandas.DataFrame()

    for token in Tokens:
        currentToken = yf.download(token, strStartDate, strDate)
        currentToken = currentToken.drop(columns=['Open', 'High', 'Close', 'Adj Close', 'Volume'])
        currentToken = currentToken.rename(columns={"Low": token + "USD"})

        if resultUSD.__len__() == 0:
            resultUSD = pandas.DataFrame(currentToken)
        else:
            resultUSD = pandas.concat([resultUSD, currentToken], axis=1)

    resultUSD = resultUSD.round(2)
    result = resultUSD

#    EURUSD = yf.download('EURUSD=X', strStartDate, strDate)
#    EURUSD = EURUSD.drop(columns=['Open', 'High', 'Close', 'Adj Close', 'Volume'])

#    resultEUR = resultUSD / EURUSD['Low']

    result['Date'] = result.index
    result = result[
        ['Date', 'TSLAUSD', 'GMEUSD', 'GOOGLUSD', 'BABAUSD', 'PLTRUSD', 'AAPLUSD', 'SPYUSD', 'QQQUSD', 'PDBCUSD',
         'VNQUSD', 'ARKKUSD', 'GLDUSD', 'URTHUSD', 'TLTUSD', 'SLVUSD']]

    index = pandas.DatetimeIndex(result['Date'])
    index = index.astype(numpy.int64).to_series() / 1000000000
    index = index.reset_index(drop="True")
    result = result.reset_index(drop="True")
    result['Date'] = index.astype(int)

    result.to_csv(pathPortfolioData + '/stockTockenPrices.portfolio', mode='w', header=True, sep=';', index=False)

    os.remove(pathPortfolioData + '/StockPricesPythonUpdate.portfolio')