#  Generate a view of the capacity of an array that grow by two

library("areaplot")

data <- matrix(ncol = 25, nrow = 4);
rownames(data) <- c("length", "capacity", "insertion", "resizing");

cost <- 1;
length <- 0;
capacity <- 2;
step <- 0;
while (step < 25) {
    data[1, step+1] <- length;
    data[2, step+1] <- capacity - length;
    data[3, step+1] <- 1;
    data[4, step+1] <- if (capacity == length) length else 0;
    if (length >= capacity) {
        capacity <- capacity * 2;
    }
    length <- length + 1;
    step <- step + 1;
}

show(data);

png("growth_by_2.png", height=20, width=18, units="cm", res=200);

par(mfrow=c(2,1))

barplot(data[c(1,2),],
        names.arg=data[1,],
        xlab="Insertions",
        ylab="Item Count",
        col=c("blue", "green"));

legend("topleft",
       c("length", "capacity"),
       cex = 0.8,
       fill = c("blue", "green"),
       box.lty=0);

## areaplot(seq(1,25), data[,c(1,2)],
##          xlab="Insertions",
##          ylab="Item Count",
##          legend=TRUE,
##          args.legend = list(x = "topleft", cex = 0.65));

barplot(data[c(3,4),],
    xlab="Insertions",
    ylab="Cost (time)",
    names.arg=seq(1,25),
    axes=TRUE,
    col=c("orange", "darkred"));

legend("topleft",
       c("insertion", "resizing"),
       cex = 0.8,
       fill = c("orange", "darkred"),
       box.lty=0);

dev.off();
