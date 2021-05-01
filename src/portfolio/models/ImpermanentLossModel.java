package portfolio.models;

public class ImpermanentLossModel {
    public double PoolCoin1; // DFI
    public double PoolCoin2; // Pair

    public ImpermanentLossModel(double PoolCoin1, double PoolCoin2){
        this.PoolCoin1 = PoolCoin1;
        this.PoolCoin2 = PoolCoin2;
    }

    public void addPooCoins(double coin1, double coin2){
        this.PoolCoin1 = this.PoolCoin1+coin1;
        this.PoolCoin2 = this.PoolCoin2+coin2;
    }
    public void removePooCoins(double coin1, double coin2){
        this.PoolCoin1 = this.PoolCoin1-coin1;
        this.PoolCoin2 = this.PoolCoin2-coin2;
    }
}
