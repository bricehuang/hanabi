package views;

public class OmnescientCardView {
    public final VisibleCardView visibleView;
    public final HiddenCardView hiddenView;

    public OmnescientCardView(
        VisibleCardView visibleView, HiddenCardView hiddenView
    ) {
        this.visibleView = visibleView;
        this.hiddenView = hiddenView;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof OmnescientCardView)) { return false; }
        OmnescientCardView that = (OmnescientCardView) other;
        return (
            this.visibleView.equals(that.visibleView) && 
            this.hiddenView.equals(that.hiddenView)
        );
    }

    @Override
    public int hashCode() {
        return this.visibleView.hashCode() + this.hiddenView.hashCode();
    }

    @Override
    public String toString() {
        return this.visibleView.toString() + " " + this.hiddenView.toString();
    }

}
